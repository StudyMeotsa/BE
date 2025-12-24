package com.example.growingstudy.security.service;

import com.example.growingstudy.auth.entity.RefreshToken;
import com.example.growingstudy.auth.repository.RefreshTokenRepository;
import com.example.growingstudy.security.dto.JwtResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * JWT의 생성, 재생성, 소모 기능 및 문자열의 토큰 변환을 제공하는 서비스
 */
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

    /**
     * 유저의 id를 subject로 하는 액세스 토큰과 리프레쉬 토큰을 생성하여 반환
     * @param userId 유저의 id
     * @return 해당 유저의 id를 subject로 하는 액세스 토큰과 리프레쉬 토큰이 담긴 DTO
     */
    @Transactional
    public JwtResponseDto generateTokens(long userId) {
        Jwt accessToken = generateAccessToken(userId);
        Jwt refreshToken = generateRefreshToken(userId);

        JwtResponseDto jwtResponseDto = new JwtResponseDto();
        jwtResponseDto.setAccessToken(accessToken.getTokenValue());
        jwtResponseDto.setRefreshToken(refreshToken.getTokenValue());

        return jwtResponseDto;
    }

    /**
     * 리프레쉬 토큰을 받아서, 액세스 토큰 및 리프레쉬 토큰을 재생성하여 반환
     * @param refreshToken 리프레쉬 토큰 문자열
     * @return 재생성된 액세스 토큰과 리프레쉬 토큰이 담긴 DTO
     */
    @Transactional
    public JwtResponseDto refreshTokens(String refreshToken) {
        long userId = consumeRefreshToken(refreshToken);

        JwtResponseDto jwtResponseDto = generateTokens(userId);
        return jwtResponseDto;
    }

    /**
     * 현재 리프레쉬 토큰을 소모하여 무효화하고, 해당 토큰의 subject(유저 id)를 반환
     * @param refreshToken 리프레쉬 토큰 문자열
     * @return 소모된 토큰의 subject (유저 id)
     */
    @Transactional
    public long consumeRefreshToken(String refreshToken) {
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

        long userId = Long.parseLong(jwt.getSubject());
        return userId;
    }

    /**
     * 유저의 id를 subject로 하는 액세스 토큰 생성
     * @param userId 유저 id
     * @return 유저의 id를 subject로 하는 액세스 타입 토큰 객체
     */
    protected Jwt generateAccessToken(long userId) {
        logger.debug("액세스 토큰 발급 시작");
        logger.trace("토큰의 헤더와 클레임 설정");
        JwtClaimsSet jwtClaimsSet =
                JwtClaimsSet
                        .builder()
                        .id(UUID.randomUUID().toString())
                        .claim("type", "access")
                        .issuer(ISSUER)
                        .issuedAt(Instant.now())
                        .subject(String.valueOf(userId))
                        .expiresAt(Instant.now().plus(Duration.ofMinutes(ACCESS_TOKEN_DURATION_MINUTES)))
                        .build();

        logger.trace("클레임 셋으로 인코딩 정보 생성");
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        logger.debug("액세스 토큰 발급 성공");
        return jwtEncoder.encode(jwtEncoderParameters);
    }

    /**
     * 유저의 id를 subject로 하는 리프레쉬 토큰 생성
     * @param userId 유저 id
     * @return 유저의 id를 subject로 하는 리프레쉬 타입 토큰 객체
     */
    @Transactional
    protected Jwt generateRefreshToken(long userId) {
        logger.debug("리프레쉬 토큰 발급 시작");
        logger.trace("토큰의 헤더와 클레임 설정");
        JwtClaimsSet jwtClaimsSet =
                JwtClaimsSet
                        .builder()
                        .id(UUID.randomUUID().toString())
                        .claim("type", "refresh")
                        .issuer(ISSUER)
                        .issuedAt(Instant.now())
                        .subject(String.valueOf(userId))
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

    /**
     * 토큰 문자열을 토큰 객체로 변환
     * @param token 토큰 문자열
     * @return 변환된 토큰 객체
     */
    public Jwt decodeTokenString(String token) {
        logger.debug("토큰 문자열 디코딩 시작");
        Jwt jwt = jwtDecoder.decode(token);
        logger.debug("토큰 문자열 디코딩 성공");
        return jwt;
    }
}
