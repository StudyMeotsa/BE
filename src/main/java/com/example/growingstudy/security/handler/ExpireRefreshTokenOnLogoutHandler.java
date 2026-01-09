package com.example.growingstudy.security.handler;

import com.example.growingstudy.security.dto.RefreshOrLogoutRequestDto;
import com.example.growingstudy.security.service.JwtService;
import com.example.growingstudy.security.util.ServletRequestConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;

public class ExpireRefreshTokenOnLogoutHandler implements LogoutHandler {

    private final Logger logger = LoggerFactory.getLogger(ExpireRefreshTokenOnLogoutHandler.class);
    private final JwtService jwtService;

    public ExpireRefreshTokenOnLogoutHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            logger.debug("리프레쉬 토큰 만료 처리");
            String requestString = ServletRequestConverter.convertRequestToString(request);
            RefreshOrLogoutRequestDto dto = ServletRequestConverter.mapJsonToDto(requestString, RefreshOrLogoutRequestDto.class);

            jwtService.consumeRefreshToken(dto.getRefreshToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            writeUnmodifiableBadRequestStatusResponse(response);
        }
    }

    private void writeUnmodifiableBadRequestStatusResponse(HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
