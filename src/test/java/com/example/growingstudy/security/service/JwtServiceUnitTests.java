package com.example.growingstudy.security.service;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.entity.OnlyUidOfRefreshToken;
import com.example.growingstudy.security.entity.RefreshToken;
import com.example.growingstudy.security.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * JwtService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceUnitTests {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtEncoder, jwtDecoder, refreshTokenRepository);
    }

    // ==================== generateTokens 테스트 ====================

    @Test
    @DisplayName("generateTokens - 액세스 토큰과 리프레쉬 토큰 생성 성공")
    void testGenerateTokens() {
        // given
        long userId = 1L;
        JwtService spyJwtService = spy(jwtService);

        Jwt mockAccessToken = createMockJwt("access-token-id", userId, "access", "access-token-value");
        Jwt mockRefreshToken = createMockJwt("refresh-token-id", userId, "refresh", "refresh-token-value");

        doReturn(mockAccessToken).when(spyJwtService).generateAccessToken(userId);
        doReturn(mockRefreshToken).when(spyJwtService).generateRefreshToken(userId);

        // when
        JwtResponseDto result = spyJwtService.generateTokens(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token-value");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-value");
        assertThat(mockAccessToken.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat(mockRefreshToken.getSubject()).isEqualTo(String.valueOf(userId));

        verify(spyJwtService).generateAccessToken(userId);
        verify(spyJwtService).generateRefreshToken(userId);
    }

    // ==================== refreshTokens 테스트 ====================

    @Test
    @DisplayName("refreshTokens - 리프레쉬 토큰으로 새 토큰 쌍 생성 성공")
    void testRefreshTokens() {
        // given
        String refreshTokenString = "valid-refresh-token";
        long userId = 1L;
        JwtService spyJwtService = spy(jwtService);

        JwtResponseDto mockJwtResponse = new JwtResponseDto();
        mockJwtResponse.setAccessToken("new-access-token");
        mockJwtResponse.setRefreshToken("new-refresh-token");

        doReturn(userId).when(spyJwtService).consumeRefreshToken(refreshTokenString);
        doReturn(mockJwtResponse).when(spyJwtService).generateTokens(userId);

        // when
        JwtResponseDto result = spyJwtService.refreshTokens(refreshTokenString);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");

        verify(spyJwtService).consumeRefreshToken(refreshTokenString);
        verify(spyJwtService).generateTokens(userId);
    }

    // ==================== consumeRefreshToken 테스트 ====================

    @Test
    @DisplayName("consumeRefreshToken - 유효한 리프레쉬 토큰 소모 성공")
    void testConsumeRefreshTokenSuccess() {
        // given
        String refreshTokenString = "valid-refresh-token";
        String jid = "refresh-token-jid";
        long userId = 1L;
        JwtService spyJwtService = spy(jwtService);

        Jwt mockJwt = createMockJwt(jid, userId, "refresh", refreshTokenString);

        doReturn(mockJwt).when(spyJwtService).decodeTokenString(refreshTokenString);

        OnlyUidOfRefreshToken mockUidProjection = () -> userId;
        given(refreshTokenRepository.findUidByJid(jid)).willReturn(mockUidProjection);

        // when
        long result = spyJwtService.consumeRefreshToken(refreshTokenString);

        // then
        assertThat(result).isEqualTo(userId);

        verify(spyJwtService).decodeTokenString(refreshTokenString);
        verify(refreshTokenRepository).findUidByJid(jid);
        verify(refreshTokenRepository).deleteById(jid);
    }

    @Test
    @DisplayName("consumeRefreshToken - 액세스 토큰 전달 시 예외 발생")
    void testConsumeRefreshTokenWithAccessTokenThrowsException() {
        // given
        String accessTokenString = "access-token-string";
        long userId = 1L;
        JwtService spyJwtService = spy(jwtService);

        Jwt mockJwt = createMockJwt("access-token-jid", userId, "access", accessTokenString);

        doReturn(mockJwt).when(spyJwtService).decodeTokenString(accessTokenString);

        // when & then
        assertThatThrownBy(() -> spyJwtService.consumeRefreshToken(accessTokenString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리프레쉬 토큰이 아님");
    }

    @Test
    @DisplayName("consumeRefreshToken - 유효하지 않은 토큰으로 예외 발생")
    void testConsumeRefreshTokenWithInvalidTokenThrowsException() {
        // given
        String invalidTokenString = "invalid-token";
        JwtService spyJwtService = spy(jwtService);

        OAuth2Error error = new OAuth2Error("invalid_token", "Invalid token", null);
        doThrow(new JwtValidationException("Invalid token", List.of(error)))
                .when(spyJwtService).decodeTokenString(invalidTokenString);

        // when & then
        assertThatThrownBy(() -> spyJwtService.consumeRefreshToken(invalidTokenString))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    // ==================== generateAccessToken 테스트 ====================

    @Test
    @DisplayName("generateAccessToken - 액세스 토큰 생성 성공")
    void testGenerateAccessToken() {
        // given
        long userId = 1L;
        Jwt mockJwt = createMockJwt("access-token-id", userId, "access", "encoded-access-token");

        given(jwtEncoder.encode(any(JwtEncoderParameters.class))).willReturn(mockJwt);

        // when
        Jwt result = jwtService.generateAccessToken(userId);

        // then
        assertThat(result).isNotNull();
        assertThat((String) result.getClaim("type")).isEqualTo("access");
        assertThat(result.getTokenValue()).isEqualTo("encoded-access-token");
        assertThat(result.getSubject()).isEqualTo(String.valueOf(userId));

        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    // ==================== generateRefreshToken 테스트 ====================

    @Test
    @DisplayName("generateRefreshToken - 리프레쉬 토큰 생성 및 저장 성공")
    void testGenerateRefreshToken() {
        // given
        long userId = 1L;
        String tokenId = "refresh-token-id";
        JwtService spyJwtService = spy(jwtService);

        Jwt mockJwt = createMockJwt(tokenId, userId, "refresh", "encoded-refresh-token");

        given(jwtEncoder.encode(any(JwtEncoderParameters.class))).willReturn(mockJwt);
        doNothing().when(spyJwtService).removePreexistingRefreshTokens(userId);

        // when
        Jwt result = spyJwtService.generateRefreshToken(userId);

        // then
        assertThat(result).isNotNull();
        assertThat((String) result.getClaim("type")).isEqualTo("refresh");
        assertThat(result.getTokenValue()).isEqualTo("encoded-refresh-token");
        assertThat(result.getSubject()).isEqualTo(String.valueOf(userId));

        verify(spyJwtService).removePreexistingRefreshTokens(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // ==================== decodeTokenString 테스트 ====================

    @Test
    @DisplayName("decodeTokenString - 토큰 문자열 디코딩 성공")
    void testDecodeTokenStringSuccess() {
        // given
        String tokenString = "valid-token-string";
        long userId = 1L;
        Jwt mockJwt = createMockJwt("token-id", userId, "access", tokenString);

        given(jwtDecoder.decode(tokenString)).willReturn(mockJwt);

        // when
        Jwt result = jwtService.decodeTokenString(tokenString);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("token-id");
        assertThat(result.getTokenValue()).isEqualTo(tokenString);

        verify(jwtDecoder).decode(tokenString);
    }

    @Test
    @DisplayName("decodeTokenString - 잘못된 토큰 문자열로 예외 발생")
    void testDecodeTokenStringWithInvalidToken() {
        // given
        String invalidTokenString = "invalid-token";

        OAuth2Error error = new OAuth2Error("invalid_token", "Invalid token", null);
        given(jwtDecoder.decode(invalidTokenString))
                .willThrow(new JwtValidationException("Invalid token", List.of(error)));

        // when & then
        assertThatThrownBy(() -> jwtService.decodeTokenString(invalidTokenString))
                .isInstanceOf(JwtValidationException.class);

        verify(jwtDecoder).decode(invalidTokenString);
    }

    // ==================== removePreexistingRefreshTokens 테스트 ====================

    @Test
    @DisplayName("removePreexistingRefreshTokens - 기존 리프레쉬 토큰들 삭제 성공")
    void testRemovePreexistingRefreshTokens() {
        // given
        long userId = 1L;
        RefreshToken token1 = new RefreshToken("jid-1", userId);
        RefreshToken token2 = new RefreshToken("jid-2", userId);
        List<RefreshToken> existingTokens = List.of(token1, token2);

        given(refreshTokenRepository.findAllByUid(userId)).willReturn(existingTokens);

        // when
        jwtService.removePreexistingRefreshTokens(userId);

        // then
        verify(refreshTokenRepository).findAllByUid(userId);
        verify(refreshTokenRepository).deleteAll(existingTokens);
    }

    @Test
    @DisplayName("removePreexistingRefreshTokens - 기존 토큰이 없는 경우")
    void testRemovePreexistingRefreshTokensWhenNoTokensExist() {
        // given
        long userId = 1L;
        List<RefreshToken> emptyList = List.of();

        given(refreshTokenRepository.findAllByUid(userId)).willReturn(emptyList);

        // when
        jwtService.removePreexistingRefreshTokens(userId);

        // then
        verify(refreshTokenRepository).findAllByUid(userId);
        verify(refreshTokenRepository).deleteAll(emptyList);
    }

    // ==================== 헬퍼 메소드 ====================

    private Jwt createMockJwt(String id, long userId, String type, String tokenValue) {
        Instant now = Instant.now();
        return new Jwt(
                tokenValue,
                now,
                now.plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of(
                        "jti", id,
                        "type", type,
                        "sub", String.valueOf(userId),
                        "iss", "http://example.com"
                )
        );
    }
}
