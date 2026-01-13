package com.example.growingstudy.security.handler;

import com.example.growingstudy.security.entity.RefreshToken;
import com.example.growingstudy.security.entity.UserDetailsWithId;
import com.example.growingstudy.security.repository.RefreshTokenRepository;
import com.example.growingstudy.security.service.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginSuccessHandler 스프링 부트 통합 테스트
 */
@SpringBootTest
class LoginSuccessHandlerSpringIntegrationTests {

    @Autowired
    private AuthenticationSuccessHandler loginSuccessHandler;

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
    @DisplayName("onAuthenticationSuccess - 인증 성공 시 JWT 토큰이 포함된 응답 반환")
    void onAuthenticationSuccessReturnsJwtTokens() throws Exception {
        // given
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("testuser")
                .password("password")
                .userId(testUserId)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertThat(jsonNode.has("accessToken")).isTrue();
        assertThat(jsonNode.has("refreshToken")).isTrue();
        assertThat(jsonNode.get("accessToken").asText()).isNotBlank();
        assertThat(jsonNode.get("refreshToken").asText()).isNotBlank();

        // 생성된 리프레쉬 토큰 ID 저장 (cleanup 용도)
        String refreshTokenString = jsonNode.get("refreshToken").asText();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 응답 Content-Type이 application/json")
    void onAuthenticationSuccessReturnsJsonContentType() throws Exception {
        // given
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("testuser")
                .password("password")
                .userId(testUserId)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        assertThat(response.getContentType()).startsWith("application/json");

        // cleanup
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String refreshTokenString = jsonNode.get("refreshToken").asText();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 응답 상태 코드가 200 OK")
    void onAuthenticationSuccessReturnsStatusOk() throws Exception {
        // given
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("testuser")
                .password("password")
                .userId(testUserId)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // cleanup
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String refreshTokenString = jsonNode.get("refreshToken").asText();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 생성된 리프레쉬 토큰이 Redis에 저장됨")
    void onAuthenticationSuccessSavesRefreshTokenToRedis() throws Exception {
        // given
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("testuser")
                .password("password")
                .userId(testUserId)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String refreshTokenString = jsonNode.get("refreshToken").asText();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        String jid = refreshJwt.getId();

        assertThat(refreshTokenRepository.findById(jid)).isPresent();
        assertThat(refreshTokenRepository.findById(jid).get().getUid()).isEqualTo(testUserId);

        createdTokenJids.add(jid);
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 생성된 토큰의 subject가 올바른 userId를 가짐")
    void onAuthenticationSuccessTokensHaveCorrectSubject() throws Exception {
        // given
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("testuser")
                .password("password")
                .userId(testUserId)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String accessTokenString = jsonNode.get("accessToken").asText();
        String refreshTokenString = jsonNode.get("refreshToken").asText();

        Jwt accessJwt = jwtService.decodeTokenString(accessTokenString);
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);

        assertThat(accessJwt.getSubject()).isEqualTo(String.valueOf(testUserId));
        assertThat(refreshJwt.getSubject()).isEqualTo(String.valueOf(testUserId));

        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 액세스 토큰 타입이 access")
    void onAuthenticationSuccessAccessTokenHasCorrectType() throws Exception {
        // given
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("testuser")
                .password("password")
                .userId(testUserId)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessTokenString = jsonNode.get("accessToken").asText();

        Jwt accessJwt = jwtService.decodeTokenString(accessTokenString);
        assertThat((String) accessJwt.getClaim("type")).isEqualTo("access");

        // cleanup
        String refreshTokenString = jsonNode.get("refreshToken").asText();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 리프레쉬 토큰 타입이 refresh")
    void onAuthenticationSuccessRefreshTokenHasCorrectType() throws Exception {
        // given
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("testuser")
                .password("password")
                .userId(testUserId)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, null, authentication);

        // then
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String refreshTokenString = jsonNode.get("refreshToken").asText();

        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        assertThat((String) refreshJwt.getClaim("type")).isEqualTo("refresh");

        createdTokenJids.add(refreshJwt.getId());
    }
}
