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
        logger.debug("액세스 토큰 발급 시작");
        logger.trace("토큰의 헤더와 클레임 설정");
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

        logger.trace("클레임 셋으로 인코딩 정보 생성");
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        logger.debug("액세스 토큰 발급 성공");
        return jwtEncoder.encode(jwtEncoderParameters);
    }

    @Transactional
    public Jwt generateRefreshToken(String username) {
        logger.debug("리프레쉬 토큰 발급 시작");
        logger.trace("토큰의 헤더와 클레임 설정");
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

        logger.trace("클레임 셋으로 인코딩 정보 생성");
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);

        Jwt refreshToken = jwtEncoder.encode(jwtEncoderParameters);
        logger.trace("발급한 리프레쉬 토큰의 ID를 DB에 저장");
        refreshTokenRepository.save(new RefreshToken(refreshToken.getId())); // 리프레쉬 토큰을 DB에 저장
        logger.debug("리프레쉬 토큰 발급 성공");
        return refreshToken;
    }

    // 현재 리프레쉬 토큰을 리스트에서 삭제하고, 리프레쉬 토큰 대상 유저의 username 반환
    @Transactional
    public String consumeRefreshToken(String refreshToken) {
        logger.debug("리프레쉬 토큰 만료 처리 시작");
        logger.trace("토큰 문자열로 토큰 객체 생성");
        Jwt jwt = decodeTokenString(refreshToken);

        if (!jwt.getClaim("type").equals("refresh")) {
            logger.debug("리프레쉬 토큰이 아님");
            throw new RuntimeException("Not a refresh token");
        }
        logger.trace("해당 리프레쉬 토큰 ID를 DB에서 삭제");
        refreshTokenRepository.deleteById(jwt.getId());
        logger.debug("리프레쉬 토큰 만료 처리 성공");

        String username = jwt.getSubject();
        return username;
    }

    public Jwt decodeTokenString(String token) {
        logger.debug("토큰 문자열 디코딩 시작");
        Jwt jwt = jwtDecoder.decode(token);
        logger.debug("토큰 문자열 디코딩 성공");
        return jwt;
    }
}
