package com.example.growingstudy.security.handler;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.entity.UserDetailsWithId;
import com.example.growingstudy.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    public LoginSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // 서비스를 통해 토큰을 생성하여 응답 반환
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        UserDetailsWithId userDetails = (UserDetailsWithId) authentication.getPrincipal();
        long userId = userDetails.getUserId();

        JwtResponseDto jwtResponseDto = jwtService.generateTokens(userId);

        // TODO: 응답에 객체 쓰기
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    }
}
