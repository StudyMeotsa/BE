package com.example.growingstudy.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// 토큰이 액세스 타입의 토큰인지 확인함
public class CheckAccessTokenFilter extends OncePerRequestFilter {
    private final String authURIPrefix = "/api/auth";
    private final List<String> uriToExclude = List.of("/me", "/mycoffees");

    // 토큰 재발급, 로그인, 로그아웃, 회원가입에 대해 미적용
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 내 정보 확인, 커피 도감은 필터 적용 대상
        return request.getRequestURI().startsWith(authURIPrefix) &&
                uriToExclude.stream().noneMatch(uri -> request.getRequestURI().endsWith(uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Jwt token = getJwtObject();

            if (token.getClaim("type") == null || !token.getClaim("type").equals("access")) {
                throw new RuntimeException("액세스 토큰이 아님");
            }
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Jwt getJwtObject() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return (Jwt) authentication.getPrincipal();
    }
}
