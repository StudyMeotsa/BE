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
