package com.example.growingstudy.security.config;

import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.security.filter.CheckAccessTokenFilter;
import com.example.growingstudy.security.filter.JsonAuthenticationProcessingFilter;
import com.example.growingstudy.security.filter.RegenerateTokensFilter;
import com.example.growingstudy.security.handler.ExpireRefreshTokenOnLogoutHandler;
import com.example.growingstudy.security.handler.LoginSuccessHandler;
import com.example.growingstudy.security.service.AccountRepositoryUserDetailsService;
import com.example.growingstudy.security.service.JwtService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
public class AuthConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AccountRepository repository) {
        return new AccountRepositoryUserDetailsService(repository);
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler(JwtService jwtService) {
        return new LoginSuccessHandler(jwtService);
    }

    @Bean
    LogoutHandler logoutHandler(JwtService jwtService) {
        return new ExpireRefreshTokenOnLogoutHandler(jwtService);
    }

    @Bean
    JsonAuthenticationProcessingFilter jsonAuthenticationProcessingFilter(AuthenticationManager authenticationManager,
                                                                          AuthenticationSuccessHandler authenticationSuccessHandler) {
        return new JsonAuthenticationProcessingFilter("/api/auth/login",
                authenticationManager, authenticationSuccessHandler);
    }

    @Bean
    CheckAccessTokenFilter checkAccessTokenFilter() {
        return new CheckAccessTokenFilter();
    }

    @Bean
    RegenerateTokensFilter regenerateTokensFilter(JwtService jwtService) {
        return new RegenerateTokensFilter(jwtService);
    }

    // 필터 이중 등록 방지를 위한 FilterRegistrationBean 설정

    @Bean
    public FilterRegistrationBean<JsonAuthenticationProcessingFilter> loginProcessingFilterRegistration(JsonAuthenticationProcessingFilter filter) {
        FilterRegistrationBean<JsonAuthenticationProcessingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CheckAccessTokenFilter> checkAccessTokenFilterRegistration(CheckAccessTokenFilter filter) {
        FilterRegistrationBean<CheckAccessTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RegenerateTokensFilter> regenerateTokensFilterRegistration(RegenerateTokensFilter filter) {
        FilterRegistrationBean<RegenerateTokensFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
