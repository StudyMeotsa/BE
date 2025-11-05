package com.example.growingstudy.security.handler;

import com.example.growingstudy.auth.dto.RefreshOrLogoutRequestDto;
import com.example.growingstudy.auth.service.JwtService;
import com.example.growingstudy.security.util.ServletRequestConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;

public class ExpireRefreshTokenOnLogoutHandler implements LogoutHandler {

    private final Logger logger = LoggerFactory.getLogger(ExpireRefreshTokenOnLogoutHandler.class);
    private final ServletRequestConverter converter;
    private final JwtService jwtService;

    public ExpireRefreshTokenOnLogoutHandler(ServletRequestConverter converter, JwtService jwtService) {
        this.converter = converter;
        this.jwtService = jwtService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            logger.info("리프레쉬 토큰 블랙리스트 추가");
            String requestString = converter.convertRequestToString(request);
            RefreshOrLogoutRequestDto dto = converter.mapJsonToDto(requestString, RefreshOrLogoutRequestDto.class);

            jwtService.consumeRefreshToken(dto.getRefreshToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
