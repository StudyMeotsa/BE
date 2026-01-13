package com.example.growingstudy.security.handler;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.entity.RefreshToken;
import com.example.growingstudy.security.repository.RefreshTokenRepository;
import com.example.growingstudy.security.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ExpireRefreshTokenOnLogoutHandler 스프링 부트 통합 테스트
 */
@SpringBootTest
class ExpireRefreshTokenOnLogoutHandlerSpringIntegrationTests {

    @Autowired
    private LogoutHandler logoutHandler;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private long testUserId;
    private List<String> createdTokenJids;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setContentType("application/json");
        response = new MockHttpServletResponse();
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

    @Test
    @DisplayName("logout - 유효한 리프레쉬 토큰으로 로그아웃 성공")
    void logoutWithValidRefreshTokenSuccess() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        String jid = refreshJwt.getId();

        assertThat(refreshTokenRepository.findById(jid)).isPresent();

        setRequestBody(refreshTokenString);

        // when
        logoutHandler.logout(request, response, null);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("logout - 로그아웃 후 리프레쉬 토큰이 Redis에서 삭제됨")
    void logoutRemovesRefreshTokenFromRedis() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        String jid = refreshJwt.getId();

        assertThat(refreshTokenRepository.findById(jid)).isPresent();

        setRequestBody(refreshTokenString);

        // when
        logoutHandler.logout(request, response, null);

        // then
        assertThat(refreshTokenRepository.findById(jid)).isEmpty();
    }

    @Test
    @DisplayName("logout - 액세스 토큰 전달 시 400 Bad Request 응답")
    void logoutWithAccessTokenReturnsBadRequest() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String accessTokenString = tokens.getAccessToken();
        Jwt refreshJwt = jwtService.decodeTokenString(tokens.getRefreshToken());

        setRequestBody(accessTokenString);

        // when
        logoutHandler.logout(request, response, null);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);

        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("logout - Redis에 없는 리프레쉬 토큰 전달 시 401 Unauthorized 응답")
    void logoutWithUnregisteredRefreshTokenReturnsUnauthorized() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        String jid = refreshJwt.getId();

        // Redis에서 토큰 삭제
        refreshTokenRepository.deleteById(jid);

        setRequestBody(refreshTokenString);

        // when
        logoutHandler.logout(request, response, null);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("logout - 잘못된 형식의 토큰 전달 시 RuntimeException 발생")
    void logoutWithInvalidTokenFormatThrowsException() throws Exception {
        // given
        String invalidToken = "invalid.token.format";
        setRequestBody(invalidToken);

        // when & then
        assertThatThrownBy(() -> logoutHandler.logout(request, response, null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("logout - 동일 리프레쉬 토큰으로 재로그아웃 시 401 Unauthorized 응답")
    void logoutWithAlreadyConsumedTokenReturnsUnauthorized() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();

        setRequestBody(refreshTokenString);

        // 첫 번째 로그아웃
        logoutHandler.logout(request, response, null);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

        // 새 request/response 준비
        request = new MockHttpServletRequest();
        request.setContentType("application/json");
        response = new MockHttpServletResponse();
        setRequestBody(refreshTokenString);

        // when - 동일 토큰으로 재로그아웃 시도
        logoutHandler.logout(request, response, null);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("logout - 다른 유저의 리프레쉬 토큰은 영향받지 않음")
    void logoutDoesNotAffectOtherUsersTokens() throws Exception {
        // given
        long otherUserId = testUserId + 1;
        JwtResponseDto tokens1 = jwtService.generateTokens(testUserId);
        JwtResponseDto tokens2 = jwtService.generateTokens(otherUserId);

        String refreshToken1 = tokens1.getRefreshToken();
        String refreshToken2 = tokens2.getRefreshToken();

        Jwt jwt1 = jwtService.decodeTokenString(refreshToken1);
        Jwt jwt2 = jwtService.decodeTokenString(refreshToken2);

        setRequestBody(refreshToken1);

        // when
        logoutHandler.logout(request, response, null);

        // then
        assertThat(refreshTokenRepository.findById(jwt1.getId())).isEmpty();
        assertThat(refreshTokenRepository.findById(jwt2.getId())).isPresent();

        // cleanup
        refreshTokenRepository.deleteById(jwt2.getId());
    }

    // ==================== 헬퍼 메소드 ====================
    
    private void setRequestBody(String refreshToken) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);
        String jsonBody = objectMapper.writeValueAsString(body);
        request.setContent(jsonBody.getBytes());
    }
}
