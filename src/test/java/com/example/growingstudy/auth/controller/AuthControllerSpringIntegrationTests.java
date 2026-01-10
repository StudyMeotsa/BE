package com.example.growingstudy.auth.controller;

import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 스프링 부트 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerSpringIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("POST /api/auth/register - 회원가입 성공")
    void testRegisterEndpointSuccess() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("테스트유저");
        request.setSex("M");

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // 실제 DB에 저장되었는지 확인
        Account savedAccount = accountRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getEmail()).isEqualTo("test@example.com");
        assertThat(savedAccount.getName()).isEqualTo("테스트유저");
        assertThat(savedAccount.getSex()).isEqualTo("M");
        assertThat(passwordEncoder.matches("password123", savedAccount.getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /api/auth/register - 회원가입 실패 (이메일 중복)")
    void testRegisterEndpointFailureWhenEmailDuplicate() throws Exception {
        // given
        Account existingAccount = Account.builder()
                .email("duplicate@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("기존유저")
                .sex("F")
                .build();
        accountRepository.save(existingAccount);

        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("duplicate@example.com");
        request.setPassword("newpassword");
        request.setPasswordConfirm("newpassword");
        request.setName("신규유저");
        request.setSex("M");

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/auth/register - Validation 실패 (이메일 형식 오류)")
    void testRegisterEndpointValidationFailureInvalidEmail() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("invalidemail");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("테스트유저");
        request.setSex("M");

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Validation 실패 (필수 필드 누락)")
    void testRegisterEndpointValidationFailureMissingRequiredFields() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        // password, name, sex 누락

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Validation 실패 (성별 패턴 오류)")
    void testRegisterEndpointValidationFailureInvalidSexPattern() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("테스트유저");
        request.setSex("X"); // M 또는 F가 아님

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Validation 실패 (비밀번호 불일치)")
    void testRegisterEndpointValidationFailurePasswordMismatch() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("differentPassword"); // 비밀번호와 불일치
        request.setName("테스트유저");
        request.setSex("M");

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Validation 실패 (비밀번호 누락, passwordConfirm만 존재)")
    void testRegisterEndpointValidationFailurePasswordNullWithPasswordConfirm() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        // password는 null (설정하지 않음)
        request.setPasswordConfirm("password123"); // passwordConfirm만 존재
        request.setName("테스트유저");
        request.setSex("M");

        // when & then
        // NullPointerException이 발생하지 않고 적절한 validation 오류로 처리되어야 함
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/auth/me - 내 정보 조회 성공")
    @WithMockUser
    void testMeEndpointSuccess() throws Exception {
        // given
        Account account = Account.builder()
                .email("me@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("내정보유저")
                .sex("M")
                .imagePath("/images/me.jpg")
                .build();
        Account savedAccount = accountRepository.save(account);

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject(String.valueOf(savedAccount.getId()))
                                .claim("type", "access"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("내정보유저"))
                .andExpect(jsonPath("$.sex").value("M"))
                .andExpect(jsonPath("$.email").value("me@example.com"));
    }

    // 기능 일관성 테스트
    @Test
    @DisplayName("GET /api/auth/me - 내 정보 조회 성공 (다른 유저)")
    @WithMockUser
    void testMeEndpointSuccessWithDifferentUser() throws Exception {
        // given
        Account account = Account.builder()
                .email("other@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("다른유저")
                .sex("F")
                .imagePath("/images/other.jpg")
                .build();
        Account savedAccount = accountRepository.save(account);

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject(String.valueOf(savedAccount.getId()))
                                .claim("type", "access"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("다른유저"))
                .andExpect(jsonPath("$.sex").value("F"))
                .andExpect(jsonPath("$.email").value("other@example.com"));
    }

    @Test
    @DisplayName("GET /api/auth/me - 내 정보 조회 실패 (존재하지 않는 유저)")
    @WithMockUser
    void testMeEndpointFailureWhenUserNotFound() throws Exception {
        // given
        long nonExistentUserId = 999999L;

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject(String.valueOf(nonExistentUserId))
                                .claim("type", "access"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/auth/me - 내 정보 조회 실패 (인증되지 않은 요청)")
    void testMeEndpointFailureWhenUnauthorized() throws Exception {
        // given
        // 인증 정보 없음

        // when & then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/register - 회원가입 후 실제 DB 등록 확인")
    void testRegisterThenLoginFlow() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("logintest@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("로그인테스트유저");
        request.setSex("M");

        // when - 회원가입
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // then - 실제 DB에 저장되었는지 확인 및 비밀번호 암호화 확인
        Account savedAccount = accountRepository.findByEmail("logintest@example.com").orElseThrow();
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getEmail()).isEqualTo("logintest@example.com");
        assertThat(savedAccount.getPassword()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", savedAccount.getPassword())).isTrue();
    }
}
