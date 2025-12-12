package com.example.growingstudy.security.filter;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.dto.RefreshOrLogoutRequestDto;
import com.example.growingstudy.security.service.JwtService;
import com.example.growingstudy.security.util.JsonResponseWriter;
import com.example.growingstudy.security.util.ServletRequestConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RegenerateTokensFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults().matcher("/api/auth/refresh");
    private final JwtService jwtService;

    public RegenerateTokensFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // 토큰 재발급 URI에서만 작동
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !requestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestBody = ServletRequestConverter.convertRequestToString(request);
        RefreshOrLogoutRequestDto dto = ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class);

        JwtResponseDto jwtResponseDto = jwtService.refreshTokens(dto.getRefreshToken());
        JsonResponseWriter.writeResponseWithDto(response, jwtResponseDto);
    }
}
