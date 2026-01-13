package com.example.growingstudy.security.service;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.entity.RefreshToken;
import com.example.growingstudy.security.repository.RefreshTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtService 스프링 부트 통합 테스트
 */
@SpringBootTest
class JwtServiceSpringIntegrationTests {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private long testUserId;
    private List<String> createdTokenJids;

    @BeforeEach
    void setUp() {
        testUserId = System.currentTimeMillis();
        createdTokenJids = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        for (String jid : createdTokenJids) {
            refreshTokenRepository.deleteById(jid);
        }
        List<RefreshToken> remainingTokens = refreshTokenRepository.findAllByUid(testUserId);
        refreshTokenRepository.deleteAll(remainingTokens);
    }

    // ==================== generateTokens 테스트 ====================

    @Test
    @DisplayName("generateTokens - 액세스 토큰과 리프레쉬 토큰 생성 성공")
    void generateTokensSuccess() {
        // given
        long userId = testUserId;

        // when
        JwtResponseDto result = jwtService.generateTokens(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getRefreshToken()).isNotBlank();

        Jwt accessJwt = jwtService.decodeTokenString(result.getAccessToken());
        Jwt refreshJwt = jwtService.decodeTokenString(result.getRefreshToken());

        assertThat((String) accessJwt.getClaim("type")).isEqualTo("access");
        assertThat(accessJwt.getSubject()).isEqualTo(String.valueOf(userId));

        assertThat((String) refreshJwt.getClaim("type")).isEqualTo("refresh");
        assertThat(refreshJwt.getSubject()).isEqualTo(String.valueOf(userId));

        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("generateTokens - 동일 유저로 재호출 시 기존 리프레쉬 토큰 삭제 및 새 토큰 생성")
    void generateTokensReplacesExistingRefreshToken() {
        // given
        long userId = testUserId;
        JwtResponseDto firstResult = jwtService.generateTokens(userId);
        Jwt firstRefreshJwt = jwtService.decodeTokenString(firstResult.getRefreshToken());
        String firstJid = firstRefreshJwt.getId();

        // when
        JwtResponseDto secondResult = jwtService.generateTokens(userId);
        Jwt secondRefreshJwt = jwtService.decodeTokenString(secondResult.getRefreshToken());
        String secondJid = secondRefreshJwt.getId();

        // then
        assertThat(firstJid).isNotEqualTo(secondJid);

        Optional<RefreshToken> oldToken = refreshTokenRepository.findById(firstJid);
        assertThat(oldToken).isEmpty();

        Optional<RefreshToken> newToken = refreshTokenRepository.findById(secondJid);
        assertThat(newToken).isPresent();
        assertThat(newToken.get().getUid()).isEqualTo(userId);

        List<RefreshToken> userTokens = refreshTokenRepository.findAllByUid(userId);
        assertThat(userTokens).hasSize(1);

        createdTokenJids.add(secondJid);
    }

    @Test
    @DisplayName("generateTokens - 리프레쉬 토큰이 Redis에 저장됨")
    void generateTokensSavesRefreshTokenToRedis() {
        // given
        long userId = testUserId;

        // when
        JwtResponseDto result = jwtService.generateTokens(userId);
        Jwt refreshJwt = jwtService.decodeTokenString(result.getRefreshToken());
        String jid = refreshJwt.getId();

        // then
        Optional<RefreshToken> savedToken = refreshTokenRepository.findById(jid);
        assertThat(savedToken).isPresent();
        assertThat(savedToken.get().getJid()).isEqualTo(jid);
        assertThat(savedToken.get().getUid()).isEqualTo(userId);

        createdTokenJids.add(jid);
    }

    // ==================== refreshTokens 테스트 ====================

    @Test
    @DisplayName("refreshTokens - 유효한 리프레쉬 토큰으로 새 토큰 쌍 생성 성공")
    void refreshTokensSuccess() {
        // given
        long userId = testUserId;
        JwtResponseDto initialTokens = jwtService.generateTokens(userId);
        String oldRefreshTokenString = initialTokens.getRefreshToken();
        Jwt oldRefreshJwt = jwtService.decodeTokenString(oldRefreshTokenString);
        String oldJid = oldRefreshJwt.getId();

        // when
        JwtResponseDto newTokens = jwtService.refreshTokens(oldRefreshTokenString);

        // then
        assertThat(newTokens).isNotNull();
        assertThat(newTokens.getAccessToken()).isNotBlank();
        assertThat(newTokens.getRefreshToken()).isNotBlank();
        assertThat(newTokens.getAccessToken()).isNotEqualTo(initialTokens.getAccessToken());
        assertThat(newTokens.getRefreshToken()).isNotEqualTo(oldRefreshTokenString);

        Jwt newAccessJwt = jwtService.decodeTokenString(newTokens.getAccessToken());
        Jwt newRefreshJwt = jwtService.decodeTokenString(newTokens.getRefreshToken());

        assertThat(newAccessJwt.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat(newRefreshJwt.getSubject()).isEqualTo(String.valueOf(userId));

        Optional<RefreshToken> oldTokenInDb = refreshTokenRepository.findById(oldJid);
        assertThat(oldTokenInDb).isEmpty();

        createdTokenJids.add(newRefreshJwt.getId());
    }

    @Test
    @DisplayName("refreshTokens - 리프레쉬 후 기존 토큰은 소모되어 재사용 불가")
    void refreshTokensConsumesOldToken() {
        // given
        long userId = testUserId;
        JwtResponseDto initialTokens = jwtService.generateTokens(userId);
        String oldRefreshTokenString = initialTokens.getRefreshToken();

        JwtResponseDto newTokens = jwtService.refreshTokens(oldRefreshTokenString);
        Jwt newRefreshJwt = jwtService.decodeTokenString(newTokens.getRefreshToken());

        // when & then
        assertThatThrownBy(() -> jwtService.refreshTokens(oldRefreshTokenString))
                .isInstanceOf(OAuth2AuthenticationException.class);

        createdTokenJids.add(newRefreshJwt.getId());
    }

    // ==================== consumeRefreshToken 테스트 ====================

    @Test
    @DisplayName("consumeRefreshToken - 유효한 리프레쉬 토큰 소모 성공")
    void consumeRefreshTokenSuccess() {
        // given
        long userId = testUserId;
        JwtResponseDto tokens = jwtService.generateTokens(userId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        String jid = refreshJwt.getId();

        // when
        long returnedUserId = jwtService.consumeRefreshToken(refreshTokenString);

        // then
        assertThat(returnedUserId).isEqualTo(userId);

        Optional<RefreshToken> consumedToken = refreshTokenRepository.findById(jid);
        assertThat(consumedToken).isEmpty();
    }

    @Test
    @DisplayName("consumeRefreshToken - 액세스 토큰 전달 시 IllegalArgumentException 발생")
    void consumeRefreshTokenWithAccessTokenThrowsException() {
        // given
        long userId = testUserId;
        JwtResponseDto tokens = jwtService.generateTokens(userId);
        String accessTokenString = tokens.getAccessToken();
        Jwt refreshJwt = jwtService.decodeTokenString(tokens.getRefreshToken());

        // when & then
        assertThatThrownBy(() -> jwtService.consumeRefreshToken(accessTokenString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리프레쉬 토큰이 아님");

        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("consumeRefreshToken - DB에 없는 리프레쉬 토큰 전달 시 OAuth2AuthenticationException 발생")
    void consumeRefreshTokenNotInDbThrowsException() {
        // given
        long userId = testUserId;
        JwtResponseDto tokens = jwtService.generateTokens(userId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        String jid = refreshJwt.getId();

        refreshTokenRepository.deleteById(jid);

        // when & then
        assertThatThrownBy(() -> jwtService.consumeRefreshToken(refreshTokenString))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("consumeRefreshToken - 잘못된 토큰 문자열 전달 시 BadJwtException 발생")
    void consumeRefreshTokenWithInvalidTokenThrowsException() {
        // given
        String invalidToken = "invalid.token.string";

        // when & then
        assertThatThrownBy(() -> jwtService.consumeRefreshToken(invalidToken))
                .isInstanceOf(BadJwtException.class);
    }

    // ==================== generateAccessToken 테스트 ====================

    @Test
    @DisplayName("generateAccessToken - 액세스 토큰 생성 및 클레임 확인")
    void generateAccessTokenSuccess() {
        // given
        long userId = testUserId;

        // when
        Jwt accessToken = jwtService.generateAccessToken(userId);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getTokenValue()).isNotBlank();
        assertThat(accessToken.getId()).isNotBlank();
        assertThat((String) accessToken.getClaim("type")).isEqualTo("access");
        assertThat(accessToken.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat(accessToken.getIssuer().toString()).isEqualTo("http://example.com");
        assertThat(accessToken.getIssuedAt()).isNotNull();
        assertThat(accessToken.getExpiresAt()).isNotNull();
        assertThat(accessToken.getExpiresAt()).isAfter(accessToken.getIssuedAt());
    }

    @Test
    @DisplayName("generateAccessToken - 생성된 토큰 디코딩 가능")
    void generateAccessTokenCanBeDecoded() {
        // given
        long userId = testUserId;
        Jwt accessToken = jwtService.generateAccessToken(userId);

        // when
        Jwt decodedToken = jwtService.decodeTokenString(accessToken.getTokenValue());

        // then
        assertThat(decodedToken.getId()).isEqualTo(accessToken.getId());
        assertThat(decodedToken.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat((String) decodedToken.getClaim("type")).isEqualTo("access");
    }

    // ==================== generateRefreshToken 테스트 ====================

    @Test
    @DisplayName("generateRefreshToken - 리프레쉬 토큰 생성 및 클레임 확인")
    void generateRefreshTokenSuccess() {
        // given
        long userId = testUserId;

        // when
        Jwt refreshToken = jwtService.generateRefreshToken(userId);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getTokenValue()).isNotBlank();
        assertThat(refreshToken.getId()).isNotBlank();
        assertThat((String) refreshToken.getClaim("type")).isEqualTo("refresh");
        assertThat(refreshToken.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat(refreshToken.getIssuer().toString()).isEqualTo("http://example.com");
        assertThat(refreshToken.getIssuedAt()).isNotNull();
        assertThat(refreshToken.getExpiresAt()).isNotNull();
        assertThat(refreshToken.getExpiresAt()).isAfter(refreshToken.getIssuedAt());

        createdTokenJids.add(refreshToken.getId());
    }

    @Test
    @DisplayName("generateRefreshToken - Redis에 저장됨")
    void generateRefreshTokenSavesToRedis() {
        // given
        long userId = testUserId;

        // when
        Jwt refreshToken = jwtService.generateRefreshToken(userId);
        String jid = refreshToken.getId();

        // then
        Optional<RefreshToken> savedToken = refreshTokenRepository.findById(jid);
        assertThat(savedToken).isPresent();
        assertThat(savedToken.get().getJid()).isEqualTo(jid);
        assertThat(savedToken.get().getUid()).isEqualTo(userId);

        createdTokenJids.add(jid);
    }

    @Test
    @DisplayName("generateRefreshToken - 기존 리프레쉬 토큰 삭제 후 새 토큰 생성")
    void generateRefreshTokenRemovesExistingTokens() {
        // given
        long userId = testUserId;
        Jwt firstToken = jwtService.generateRefreshToken(userId);
        String firstJid = firstToken.getId();

        // when
        Jwt secondToken = jwtService.generateRefreshToken(userId);
        String secondJid = secondToken.getId();

        // then
        assertThat(firstJid).isNotEqualTo(secondJid);

        Optional<RefreshToken> oldToken = refreshTokenRepository.findById(firstJid);
        assertThat(oldToken).isEmpty();

        Optional<RefreshToken> newToken = refreshTokenRepository.findById(secondJid);
        assertThat(newToken).isPresent();

        List<RefreshToken> userTokens = refreshTokenRepository.findAllByUid(userId);
        assertThat(userTokens).hasSize(1);

        createdTokenJids.add(secondJid);
    }

    // ==================== decodeTokenString 테스트 ====================

    @Test
    @DisplayName("decodeTokenString - 유효한 토큰 디코딩 성공")
    void decodeTokenStringSuccess() {
        // given
        long userId = testUserId;
        Jwt token = jwtService.generateAccessToken(userId); // 액세스, 리프레쉬 여부 무관
        String tokenString = token.getTokenValue();

        // when
        Jwt decodedToken = jwtService.decodeTokenString(tokenString);

        // then
        assertThat(decodedToken).isNotNull();
        assertThat(decodedToken.getId()).isEqualTo(token.getId());
        assertThat(decodedToken.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat((String) decodedToken.getClaim("type")).isEqualTo("access");
    }

    @Test
    @DisplayName("decodeTokenString - 잘못된 형식의 토큰 문자열로 BadJwtException 발생")
    void decodeTokenStringWithInvalidFormatThrowsException() {
        // given
        String invalidToken = "invalid.token.format";

        // when & then
        assertThatThrownBy(() -> jwtService.decodeTokenString(invalidToken))
                .isInstanceOf(BadJwtException.class);
    }

    @Test
    @DisplayName("decodeTokenString - 빈 문자열로 예외 발생")
    void decodeTokenStringWithEmptyStringThrowsException() {
        // given
        String emptyToken = "";

        // when & then
        assertThatThrownBy(() -> jwtService.decodeTokenString(emptyToken))
                .isInstanceOf(Exception.class);
    }

    // ==================== removePreexistingRefreshTokens 테스트 ====================

    @Test
    @DisplayName("removePreexistingRefreshTokens - 해당 유저의 기존 리프레쉬 토큰 모두 삭제")
    void removePreexistingRefreshTokensSuccess() {
        // given
        long userId = testUserId;

        RefreshToken token1 = new RefreshToken("test-jid-1-" + System.currentTimeMillis(), userId);
        RefreshToken token2 = new RefreshToken("test-jid-2-" + System.currentTimeMillis(), userId);
        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);

        List<RefreshToken> beforeRemoval = refreshTokenRepository.findAllByUid(userId);
        assertThat(beforeRemoval).hasSize(2);

        // when
        jwtService.removePreexistingRefreshTokens(userId);

        // then
        List<RefreshToken> afterRemoval = refreshTokenRepository.findAllByUid(userId);
        assertThat(afterRemoval).isEmpty();
    }

    @Test
    @DisplayName("removePreexistingRefreshTokens - 기존 토큰이 없는 경우에도 정상 동작")
    void removePreexistingRefreshTokensWithNoExistingTokens() {
        // given
        long userId = testUserId;
        List<RefreshToken> existingTokens = refreshTokenRepository.findAllByUid(userId);
        refreshTokenRepository.deleteAll(existingTokens);

        // when & then (예외 발생하지 않음)
        jwtService.removePreexistingRefreshTokens(userId);

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(userId);
        assertThat(tokens).isEmpty();
    }

    @Test
    @DisplayName("removePreexistingRefreshTokens - 다른 유저의 토큰은 영향받지 않음")
    void removePreexistingRefreshTokensDoesNotAffectOtherUsers() {
        // given
        long userId1 = testUserId;
        long userId2 = testUserId + 1;

        RefreshToken token1 = new RefreshToken("user1-token-" + System.currentTimeMillis(), userId1);
        RefreshToken token2 = new RefreshToken("user2-token-" + System.currentTimeMillis(), userId2);
        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);

        // when
        jwtService.removePreexistingRefreshTokens(userId1);

        // then
        List<RefreshToken> user1Tokens = refreshTokenRepository.findAllByUid(userId1);
        assertThat(user1Tokens).isEmpty();

        List<RefreshToken> user2Tokens = refreshTokenRepository.findAllByUid(userId2);
        assertThat(user2Tokens).hasSize(1);
        assertThat(user2Tokens.get(0).getUid()).isEqualTo(userId2);

        refreshTokenRepository.deleteById(token2.getJid());
    }
}
