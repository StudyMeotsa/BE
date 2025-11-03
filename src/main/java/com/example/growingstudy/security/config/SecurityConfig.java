package com.example.growingstudy.security.config;

import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.security.filter.JsonAuthenticationProcessingFilter;
import com.example.growingstudy.security.service.AccountRepositoryUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    UserDetailsService userDetailsService(AccountRepository repository) {
        return new AccountRepositoryUserDetailsService(repository);
    }

    AuthenticationManager authenticationManager(AccountRepository repository) {
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(userDetailsService(repository));
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AccountRepository repository) throws Exception {
        return http
                .csrf((csrf) -> csrf.disable())
                .logout((logout) -> logout
                        .logoutUrl("/auth/logout")
                        .permitAll()
                )
                .formLogin((form) -> form.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("**").authenticated()
                )
                .addFilterAt(
                        new JsonAuthenticationProcessingFilter("/auth/login", authenticationManager(repository)),
                        UsernamePasswordAuthenticationFilter.class
                )
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .sessionManagement((session) -> session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }
}
