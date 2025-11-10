package com.example.growingstudy.security.filter;

import com.example.growingstudy.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 토큰이 액세스 타입의 토큰인지 확인함
public class CheckAccessTokenFilter extends OncePerRequestFilter {
    private final String authURIPrefix = "/api/auth";
    private final JwtService jwtService;

    public CheckAccessTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // 토큰 재발급, 로그인, 로그아웃, 회원가입에 대해 미적용
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith(authURIPrefix);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Jwt token = jwtService.decodeTokenString(request.getHeader("Authorization").substring(6));
        if (!token.getClaim("type").equals("access")) throw new RuntimeException("액세스 토큰이 아님");
        filterChain.doFilter(request, response);
    }
}
