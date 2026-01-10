package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.MyPageResponseDto;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.enums.RegisterFailedType;
import com.example.growingstudy.auth.exception.RegisterFailedException;
import com.example.growingstudy.auth.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AuthService 스프링 부트 통합 테스트
 */
@SpringBootTest
@Transactional
class AuthServiceSpringIntegrationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 시 비밀번호 암호화 확인 테스트")
    void testRegisterPasswordEncryption() {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("encrypt@example.com");
        request.setPassword("plainPassword123");
        request.setPasswordConfirm("plainPassword123");
        request.setName("암호화테스트유저");
        request.setSex("M");

        // when
        authService.register(request);

        // then
        Account savedAccount = accountRepository.findByEmail("encrypt@example.com").orElseThrow();
        assertThat(savedAccount.getPassword()).isNotEqualTo("plainPassword123");
        assertThat(passwordEncoder.matches("plainPassword123", savedAccount.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void testRegisterSuccess() {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("테스트유저");
        request.setSex("M");

        // when
        authService.register(request);

        // then
        Account savedAccount = accountRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getEmail()).isEqualTo("test@example.com");
        assertThat(passwordEncoder.matches("password123", savedAccount.getPassword())).isTrue();
        assertThat(savedAccount.getName()).isEqualTo("테스트유저");
        assertThat(savedAccount.getSex()).isEqualTo("M");
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 중복")
    void testRegisterFailureWhenEmailDuplicate() {
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
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RegisterFailedException.class)
                .hasMessage(RegisterFailedType.USERNAME_NOT_UNIQUE.getMessage())
                .extracting(ex -> ((RegisterFailedException) ex).getType())
                .isEqualTo(RegisterFailedType.USERNAME_NOT_UNIQUE);
    }

    @Test
    @DisplayName("유효성 검증 성공 테스트 - 중복이 없는 경우")
    void testValidateUniquenessSuccess() {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("unique@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("유니크유저");
        request.setSex("F");

        // when
        authService.validateUniqueness(request);

        // then
        // 예외가 발생하지 않으면 성공
        assertThat(accountRepository.existsByEmail("unique@example.com")).isFalse();
    }

    @Test
    @DisplayName("유효성 검증 실패 테스트 - 이메일 중복인 경우")
    void testValidateUniquenessFailureWhenEmailDuplicate() {
        // given
        Account existingAccount = Account.builder()
                .email("duplicate@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("기존유저")
                .sex("M")
                .build();
        accountRepository.save(existingAccount);

        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("duplicate@example.com");
        request.setPassword("newpassword");
        request.setPasswordConfirm("newpassword");
        request.setName("신규유저");
        request.setSex("F");

        // when & then
        assertThatThrownBy(() -> authService.validateUniqueness(request))
                .isInstanceOf(RegisterFailedException.class)
                .hasMessage(RegisterFailedType.USERNAME_NOT_UNIQUE.getMessage())
                .extracting(ex -> ((RegisterFailedException) ex).getType())
                .isEqualTo(RegisterFailedType.USERNAME_NOT_UNIQUE);
    }

    @Test
    @DisplayName("유저 정보 조회 성공 테스트")
    void testRetrieveUserInfoSuccess() {
        // given
        Account account = Account.builder()
                .email("info@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("정보조회유저")
                .sex("F")
                .imagePath("/images/user.jpg")
                .build();
        Account savedAccount = accountRepository.save(account);

        // when
        MyPageResponseDto result = authService.retrieveUserInfo(savedAccount.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("정보조회유저");
        assertThat(result.getSex()).isEqualTo("F");
        assertThat(result.getEmail()).isEqualTo("info@example.com");
    }

    @Test
    @DisplayName("유저 정보 조회 실패 테스트 - 유저가 존재하지 않는 경우")
    void testRetrieveUserInfoFailureWhenUserNotFound() {
        // given
        long nonExistentUserId = 999999L;

        // when & then
        assertThatThrownBy(() -> authService.retrieveUserInfo(nonExistentUserId))
                .isInstanceOf(NoSuchElementException.class);
    }
}
