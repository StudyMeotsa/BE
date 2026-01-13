package com.example.growingstudy.security.filter;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.entity.RefreshToken;
import com.example.growingstudy.security.repository.RefreshTokenRepository;
import com.example.growingstudy.security.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CheckAccessTokenFilter 스프링 부트 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class CheckAccessTokenFilterSpringIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "test-check-filter@example.com";
    private static final String TEST_PASSWORD = "testPassword123!";

    private Account testAccount;
    private String accessToken;
    private String refreshToken;

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

        // 토큰 생성
        JwtResponseDto tokens = jwtService.generateTokens(testAccount.getId());
        accessToken = tokens.getAccessToken();
        refreshToken = tokens.getRefreshToken();
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

    // ==================== 액세스 토큰으로 접근 테스트 ====================

    @Test
    @DisplayName("doFilterInternal - 액세스 토큰으로 /api/auth/me 접근 성공")
    void accessTokenAllowsAccessToMeEndpoint() throws Exception {
        // given & when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    // ==================== 리프레쉬 토큰으로 접근 테스트 ====================

    @Test
    @DisplayName("doFilterInternal - 리프레쉬 토큰으로 /api/auth/me 접근 시 401 응답")
    void refreshTokenDeniesAccessToMeEndpoint() throws Exception {
        // given & when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }

    // ==================== shouldNotFilter 테스트 ====================

    @Test
    @DisplayName("shouldNotFilter - /api/auth/login 경로는 필터 미적용")
    void loginEndpointBypassesFilter() throws Exception {
        // given & when & then
        // 로그인 엔드포인트는 토큰 없이도 접근 가능
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized()); // 인증 실패지만 필터는 통과
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/register 경로는 필터 미적용")
    void registerEndpointBypassesFilter() throws Exception {
        // given & when & then
        // 회원가입 엔드포인트는 토큰 없이도 접근 가능
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"newuser@test.com\",\"password\":\"Test1234!\",\"passwordConfirm\":\"Test1234!\",\"name\":\"New User\",\"sex\":\"M\"}"))
                .andExpect(status().isCreated());

        // cleanup
        accountRepository.findByEmail("newuser@test.com").ifPresent(accountRepository::delete);
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/refresh 경로는 필터 미적용")
    void refreshEndpointBypassesFilter() throws Exception {
        // given & when & then
        // 토큰 재발급 엔드포인트는 필터 미적용
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/logout 경로는 필터 미적용")
    void logoutEndpointBypassesFilter() throws Exception {
        // given
        // 새 토큰 생성 (logout에서 소모될 예정)
        JwtResponseDto newTokens = jwtService.generateTokens(testAccount.getId());
        String newRefreshToken = newTokens.getRefreshToken();

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + newRefreshToken + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("shouldNotFilter - 그 외 경로는 필터 적용")
    void otherEndpointDoesNotBypassesFilter() throws Exception {
        mockMvc.perform(get("/api/other"))
                // 그 외 경로에는 필터가 적용되므로 401 Unauthorized여야 함
                .andExpect(status().isUnauthorized());
    }

    // ==================== 토큰 없이 접근 테스트 ====================

    @Test
    @DisplayName("doFilterInternal - 토큰 없이 /api/auth/me 접근 시 401 응답")
    void noTokenDeniesAccessToMeEndpoint() throws Exception {
        // given & when & then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("doFilterInternal - 잘못된 형식의 토큰으로 접근 시 401 응답")
    void invalidTokenDeniesAccess() throws Exception {
        // given
        String invalidToken = "invalid.token.format";

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }
}
