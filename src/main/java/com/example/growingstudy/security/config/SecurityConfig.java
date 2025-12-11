package com.example.growingstudy.security.config;

import com.example.growingstudy.security.filter.CheckAccessTokenFilter;
import com.example.growingstudy.security.filter.JsonAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LogoutHandler logoutHandler; // 로그아웃 핸들러 (리프레쉬 토큰 무효화)
    private final JsonAuthenticationProcessingFilter jsonAuthenticationProcessingFilter; // JSON 방식의 로그인 필터
    private final CheckAccessTokenFilter checkAccessTokenFilter; // 액세스 토큰 여부 확인 필터

    @Autowired
    public SecurityConfig(LogoutHandler logoutHandler, JsonAuthenticationProcessingFilter jsonAuthenticationProcessingFilter,
                          CheckAccessTokenFilter checkAccessTokenFilter) {
        this.logoutHandler = logoutHandler;
        this.jsonAuthenticationProcessingFilter = jsonAuthenticationProcessingFilter;
        this.checkAccessTokenFilter = checkAccessTokenFilter;
    }

    // H2 콘솔 및 스웨거에 대해 시큐리티 미적용 설정 (로컬 테스트용)
    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true") // H2가 있을 때만 유효한 설정
    WebSecurityCustomizer disableSecurityforh2Console() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    WebSecurityCustomizer disableSecurityforSwagger() {
        return web -> web.ignoring()
                .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**");
    }

    // 시큐리티 설정 본체
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf((csrf) -> csrf.disable())
                .logout((logout) -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                        .permitAll()
                )
                .formLogin((form) -> form.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/**").authenticated()
                )
                .addFilterAt(jsonAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(checkAccessTokenFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .sessionManagement((session) -> session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }
}
