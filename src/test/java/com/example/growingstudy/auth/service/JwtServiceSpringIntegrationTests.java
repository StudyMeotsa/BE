package com.example.growingstudy.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JwtServiceSpringIntegrationTests {

    private static final String ISSUER = "http://example.com";

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("액세스 토큰 정상 발급")
    public void generateAccessToken() {
        Jwt jwt = jwtService.generateAccessToken();

        // jwt.getIssuer()는 URL 객체를 반환
        assertEquals(ISSUER, jwt.getIssuer().toString());
    }

    @Test
    @DisplayName("리프레쉬 토큰 정상 발급")
    public void generateRefreshToken() {
        Jwt jwt = jwtService.generateRefreshToken();

        // jwt.getIssuer()는 URL 객체를 반환
        assertEquals(ISSUER, jwt.getIssuer().toString());
    }
}
