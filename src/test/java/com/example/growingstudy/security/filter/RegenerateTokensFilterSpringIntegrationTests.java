package com.example.growingstudy.security.filter;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.entity.RefreshToken;
import com.example.growingstudy.security.repository.RefreshTokenRepository;
import com.example.growingstudy.security.service.JwtService;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RegenerateTokensFilter 스프링 부트 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class RegenerateTokensFilterSpringIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REFRESH_URL = "/api/auth/refresh";

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

    // ==================== 토큰 재발급 성공 테스트 ====================

    @Test
    @DisplayName("doFilterInternal - 유효한 리프레쉬 토큰으로 토큰 재발급 성공")
    void refreshWithValidTokenSuccess() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();

        // when & then
        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("doFilterInternal - 재발급 시 새 액세스/리프레쉬 토큰 반환")
    void refreshReturnsNewTokens() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String oldRefreshToken = tokens.getRefreshToken();

        // when
        MvcResult result = mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(oldRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertThat(jsonNode.has("accessToken")).isTrue();
        assertThat(jsonNode.has("refreshToken")).isTrue();

        String newAccessToken = jsonNode.get("accessToken").asText();
        String newRefreshToken = jsonNode.get("refreshToken").asText();

        assertThat(newAccessToken).isNotBlank();
        assertThat(newRefreshToken).isNotBlank();
        assertThat(newRefreshToken).isNotEqualTo(oldRefreshToken);

        // cleanup
        Jwt newRefreshJwt = jwtService.decodeTokenString(newRefreshToken);
        createdTokenJids.add(newRefreshJwt.getId());
    }

    @Test
    @DisplayName("doFilterInternal - 재발급된 토큰의 subject가 동일한 userId를 가짐")
    void refreshedTokensHaveSameSubject() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();

        // when
        MvcResult result = mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String newAccessToken = jsonNode.get("accessToken").asText();
        String newRefreshToken = jsonNode.get("refreshToken").asText();

        Jwt newAccessJwt = jwtService.decodeTokenString(newAccessToken);
        Jwt newRefreshJwt = jwtService.decodeTokenString(newRefreshToken);

        assertThat(newAccessJwt.getSubject()).isEqualTo(String.valueOf(testUserId));
        assertThat(newRefreshJwt.getSubject()).isEqualTo(String.valueOf(testUserId));

        createdTokenJids.add(newRefreshJwt.getId());
    }

    @Test
    @DisplayName("doFilterInternal - 재발급 시 기존 리프레쉬 토큰 소모됨")
    void refreshConsumesOldToken() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String oldRefreshToken = tokens.getRefreshToken();
        Jwt oldRefreshJwt = jwtService.decodeTokenString(oldRefreshToken);
        String oldJid = oldRefreshJwt.getId();

        assertThat(refreshTokenRepository.findById(oldJid)).isPresent();

        // when
        MvcResult result = mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(oldRefreshToken)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        assertThat(refreshTokenRepository.findById(oldJid)).isEmpty();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String newRefreshToken = jsonNode.get("refreshToken").asText();
        Jwt newRefreshJwt = jwtService.decodeTokenString(newRefreshToken);

        createdTokenJids.add(newRefreshJwt.getId());
    }

    @Test
    @DisplayName("doFilterInternal - 재발급 후 새 리프레쉬 토큰이 Redis에 저장됨")
    void refreshSavesNewTokenToRedis() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();

        // when
        MvcResult result = mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String newRefreshToken = jsonNode.get("refreshToken").asText();
        Jwt newRefreshJwt = jwtService.decodeTokenString(newRefreshToken);
        String newJid = newRefreshJwt.getId();

        assertThat(refreshTokenRepository.findById(newJid)).isPresent();
        assertThat(refreshTokenRepository.findById(newJid).get().getUid()).isEqualTo(testUserId);

        createdTokenJids.add(newJid);
    }

    // ==================== 토큰 재발급 실패 테스트 ====================

    @Test
    @DisplayName("doFilterInternal - 액세스 토큰으로 재발급 시도 시 400 Bad Request")
    void refreshWithAccessTokenReturnsBadRequest() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String accessToken = tokens.getAccessToken();
        Jwt refreshJwt = jwtService.decodeTokenString(tokens.getRefreshToken());

        // when & then
        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(accessToken)))
                .andExpect(status().isBadRequest());

        createdTokenJids.add(refreshJwt.getId());
    }

    @Test
    @DisplayName("doFilterInternal - Redis에 없는 리프레쉬 토큰으로 재발급 시도 시 401 Unauthorized")
    void refreshWithUnregisteredTokenReturnsUnauthorized() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);
        String jid = refreshJwt.getId();

        // Redis에서 토큰 삭제
        refreshTokenRepository.deleteById(jid);

        // when & then
        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("doFilterInternal - 이미 소모된 리프레쉬 토큰으로 재발급 시도 시 401 Unauthorized")
    void refreshWithConsumedTokenReturnsUnauthorized() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();

        // 첫 번째 재발급
        MvcResult firstResult = mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isOk())
                .andReturn();

        String firstResponse = firstResult.getResponse().getContentAsString();
        JsonNode firstJson = objectMapper.readTree(firstResponse);
        String newRefreshToken = firstJson.get("refreshToken").asText();
        Jwt newRefreshJwt = jwtService.decodeTokenString(newRefreshToken);

        // when - 동일 토큰으로 재발급 시도
        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isUnauthorized());

        createdTokenJids.add(newRefreshJwt.getId());
    }

    // ==================== shouldNotFilter 테스트 ====================

    @Test
    @DisplayName("shouldNotFilter - /api/auth/refresh 경로에서만 필터 작동")
    void filterOnlyWorksOnRefreshEndpoint() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);

        // when & then - /api/auth/refresh 경로에서는 필터 작동
        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("shouldNotFilter - 다른 경로에서는 필터 미작동")
    void filterDoesNotWorkOnOtherEndpoint() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();
        Jwt refreshJwt = jwtService.decodeTokenString(refreshTokenString);

        // when & then - 필터 미작동하고, 헤더에 액세스 토큰 없으므로 401 Unauthorized
        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("doFilterInternal - 응답 Content-Type이 application/json")
    void refreshReturnsJsonContentType() throws Exception {
        // given
        JwtResponseDto tokens = jwtService.generateTokens(testUserId);
        String refreshTokenString = tokens.getRefreshToken();

        // when & then
        MvcResult result = mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshRequestJson(refreshTokenString)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String newRefreshToken = jsonNode.get("refreshToken").asText();
        Jwt newRefreshJwt = jwtService.decodeTokenString(newRefreshToken);

        createdTokenJids.add(newRefreshJwt.getId());
    }

    // ==================== 헬퍼 메소드 ====================

    private String createRefreshRequestJson(String refreshToken) {
        return "{\"refreshToken\":\"" + refreshToken + "\"}";
    }
}
