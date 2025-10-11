package com.example.growingstudy.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {

    // 상수 영역
    private static final String ISSUER = "example.com"; // 추후에 도메인 정해지면 변경
    private static final long ACCESS_TOKEN_DURATION_MINUTES = 5;
    private static final long REFRESH_TOKEN_DURATION_HOURS = 1;

    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public Jwt generateAccessToken() {
        logger.info("액세스 토큰 발급 시작");
        JwtClaimsSet jwtClaimsSet =
                JwtClaimsSet
                        .builder()
                        .issuer(ISSUER)
                        .issuedAt(Instant.now())
                        .subject("anonymous")
                        .expiresAt(Instant.now().plus(Duration.ofMinutes(ACCESS_TOKEN_DURATION_MINUTES)))
                        .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        logger.info("액세스 토큰 발급 완료");
        return jwtEncoder.encode(jwtEncoderParameters);
    }

    public Jwt generateRefreshToken() {
        logger.info("리프레쉬 토큰 발급 시작");
        JwtClaimsSet jwtClaimsSet =
                JwtClaimsSet
                        .builder()
                        .issuer(ISSUER)
                        .issuedAt(Instant.now())
                        .subject("anonymous")
                        .expiresAt(Instant.now().plus(Duration.ofHours(REFRESH_TOKEN_DURATION_HOURS)))
                        .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        logger.info("리프레쉬 토큰 발급 완료");
        return jwtEncoder.encode(jwtEncoderParameters);
    }
}
