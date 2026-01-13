package com.example.growingstudy.security.filter;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.security.entity.RefreshToken;
import com.example.growingstudy.security.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JsonAuthenticationProcessingFilter 스프링 부트 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class JsonAuthenticationProcessingFilterSpringIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String TEST_EMAIL = "test-filter@example.com";
    private static final String TEST_PASSWORD = "testPassword123!";

    private Account testAccount;

    @BeforeEach
    void setUp() {
        // 테스트용 계정 생성
        testAccount = Account.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .name("Test User")
                .sex("M")
                .build();
        testAccount = accountRepository.save(testAccount);
    }

    @AfterEach
    void tearDown() {
        // 생성된 리프레쉬 토큰 정리
        if (testAccount != null && testAccount.getId() != null) {
            List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(testAccount.getId());
            refreshTokenRepository.deleteAll(tokens);
        }
        // 테스트 계정 삭제
        accountRepository.findByEmail(TEST_EMAIL).ifPresent(accountRepository::delete);
    }

    // ==================== attemptAuthentication 테스트 ====================

    @Test
    @DisplayName("attemptAuthentication - 유효한 이메일/비밀번호로 인증 성공")
    void attemptAuthenticationWithValidCredentialsSuccess() throws Exception {
        // given
        String requestBody = createLoginRequestJson(TEST_EMAIL, TEST_PASSWORD);

        // when & then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("attemptAuthentication - 인증 성공 시 JWT 토큰 응답 반환")
    void attemptAuthenticationReturnsJwtTokens() throws Exception {
        // given
        String requestBody = createLoginRequestJson(TEST_EMAIL, TEST_PASSWORD);

        // when
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertThat(jsonNode.has("accessToken")).isTrue();
        assertThat(jsonNode.has("refreshToken")).isTrue();
        assertThat(jsonNode.get("accessToken").asText()).isNotBlank();
        assertThat(jsonNode.get("refreshToken").asText()).isNotBlank();
    }

    @Test
    @DisplayName("attemptAuthentication - 존재하지 않는 이메일로 인증 실패")
    void attemptAuthenticationWithNonExistentEmailFails() throws Exception {
        // given
        String requestBody = createLoginRequestJson("nonexistent@example.com", TEST_PASSWORD);

        // when & then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("attemptAuthentication - 잘못된 비밀번호로 인증 실패")
    void attemptAuthenticationWithWrongPasswordFails() throws Exception {
        // given
        String requestBody = createLoginRequestJson(TEST_EMAIL, "wrongPassword!");

        // when & then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("attemptAuthentication - 빈 이메일로 인증 실패")
    void attemptAuthenticationWithEmptyEmailFails() throws Exception {
        // given
        String requestBody = createLoginRequestJson("", TEST_PASSWORD);

        // when & then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("attemptAuthentication - 빈 비밀번호로 인증 실패")
    void attemptAuthenticationWithEmptyPasswordFails() throws Exception {
        // given
        String requestBody = createLoginRequestJson(TEST_EMAIL, "");

        // when & then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("attemptAuthentication - 요청 본문 없이 요청 시 실패")
    void attemptAuthenticationWithEmptyBodyFails() throws Exception {
        // given & when & then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isUnauthorized());
    }

    // ==================== successfulAuthentication 테스트 ====================

    @Test
    @DisplayName("successfulAuthentication - 인증 성공 시 리프레쉬 토큰이 Redis에 저장됨")
    void successfulAuthenticationSavesRefreshTokenToRedis() throws Exception {
        // given
        String requestBody = createLoginRequestJson(TEST_EMAIL, TEST_PASSWORD);

        // when
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // then
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(testAccount.getId());
        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0).getUid()).isEqualTo(testAccount.getId());
    }

    @Test
    @DisplayName("successfulAuthentication - 재로그인 시 기존 리프레쉬 토큰 삭제 후 새 토큰 저장")
    void successfulAuthenticationReplacesExistingRefreshToken() throws Exception {
        // given
        String requestBody = createLoginRequestJson(TEST_EMAIL, TEST_PASSWORD);

        // 첫 번째 로그인
        MvcResult firstResult = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String firstResponse = firstResult.getResponse().getContentAsString();
        JsonNode firstJson = objectMapper.readTree(firstResponse);
        String firstRefreshToken = firstJson.get("refreshToken").asText();

        // when - 두 번째 로그인
        MvcResult secondResult = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String secondResponse = secondResult.getResponse().getContentAsString();
        JsonNode secondJson = objectMapper.readTree(secondResponse);
        String secondRefreshToken = secondJson.get("refreshToken").asText();

        // then
        assertThat(firstRefreshToken).isNotEqualTo(secondRefreshToken);

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(testAccount.getId());
        assertThat(tokens).hasSize(1);
    }

    // ==================== 헬퍼 메소드 ====================

    private String createLoginRequestJson(String email, String password) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        return objectMapper.writeValueAsString(body);
    }
}
