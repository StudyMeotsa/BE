package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.entity.RefreshToken;
import com.example.growingstudy.auth.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class JwtService {

    // 상수 영역
    private static final String ISSUER = "http://example.com"; // 추후에 도메인 정해지면 변경
    private static final long ACCESS_TOKEN_DURATION_MINUTES = 5;
    private static final long REFRESH_TOKEN_DURATION_HOURS = 1;

    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder,
                      RefreshTokenRepository refreshTokenRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Jwt generateAccessToken(String username) {
        logger.info("액세스 토큰 발급 시작");
        JwtClaimsSet jwtClaimsSet =
                JwtClaimsSet
                        .builder()
                        .id(UUID.randomUUID().toString())
                        .claim("type", "access")
                        .issuer(ISSUER)
                        .issuedAt(Instant.now())
                        .subject(username)
                        .expiresAt(Instant.now().plus(Duration.ofMinutes(ACCESS_TOKEN_DURATION_MINUTES)))
                        .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        logger.info("액세스 토큰 발급 완료");
        return jwtEncoder.encode(jwtEncoderParameters);
    }

    public Jwt generateRefreshToken(String username) {
        logger.info("리프레쉬 토큰 발급 시작");
        JwtClaimsSet jwtClaimsSet =
                JwtClaimsSet
                        .builder()
                        .id(UUID.randomUUID().toString())
                        .claim("type", "refresh")
                        .issuer(ISSUER)
                        .issuedAt(Instant.now())
                        .subject(username)
                        .expiresAt(Instant.now().plus(Duration.ofHours(REFRESH_TOKEN_DURATION_HOURS)))
                        .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);

        Jwt refreshToken = jwtEncoder.encode(jwtEncoderParameters);
        logger.info("리프레쉬 토큰 발급 완료");
        refreshTokenRepository.save(new RefreshToken(refreshToken.getId())); // 리프레쉬 토큰을 DB에 저장
        return refreshToken;
    }

    // 현재 리프레쉬 토큰을 리스트에서 삭제하고, 리프레쉬 토큰 대상 유저의 username 반환
    @Transactional
    public String consumeRefreshToken(String refreshToken) {
        Jwt jwt = decodeTokenString(refreshToken);

        if (!jwt.getClaim("type").equals("refresh")) {
            throw new RuntimeException("Not a refresh token");
        }
        refreshTokenRepository.deleteById(jwt.getId());
        logger.info("리프레쉬 토큰 만료 처리 완료");

        String username = jwt.getSubject();
        return username;
    }

    private Jwt decodeTokenString(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        logger.info("토큰 문자열 디코딩 완료");
        return jwt;
    }
}
