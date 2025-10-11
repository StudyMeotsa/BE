package com.example.growingstudy.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class JwtServiceUnitTests {

    // 상수 영역
    private static final String ISSUER = "http://example.com"; // 추후에 도메인 정해지면 변경
    private static final long ACCESS_TOKEN_DURATION_MINUTES = 5;
    private static final long REFRESH_TOKEN_DURATION_HOURS = 1;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtService jwtService;

    @Test
    @DisplayName("액세스 토큰 정상 발급")
    public void generateAccessToken() {
        // given
        given(jwtEncoder.encode(any()))
                .willReturn(Jwt
                        .withTokenValue("access")
                        .header("testHeader", "")
                        .claim("testClaim", "")
                        .build());

        // when
        Jwt accessToken = jwtService.generateAccessToken();

        // then
        assertEquals("access", accessToken.getTokenValue());
    }

    @Test
    @DisplayName("리프레쉬 토큰 정상 발급")
    public void generateRefreshToken() {
        // given
        given(jwtEncoder.encode(any()))
                .willReturn(Jwt
                        .withTokenValue("refresh")
                        .header("testHeader", "")
                        .claim("testClaim", "")
                        .build());

        // when
        Jwt refreshToken = jwtService.generateRefreshToken();

        // then
        assertEquals("refresh", refreshToken.getTokenValue());
    }
}
