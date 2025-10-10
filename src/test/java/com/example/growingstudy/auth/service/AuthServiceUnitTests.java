package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.exception.PasswordConfirmIncorrectException;
import com.example.growingstudy.auth.exception.UserAlreadyExistsException;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    // 유효성 검증

    @Test
    @DisplayName("유효성 검증 성공")
    public void successfulValidation() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        assertDoesNotThrow(() -> authService.validate(request));
    }

    @Test
    @DisplayName("이미 유저 존재하여 유효성 검증 실패")
    public void failedValidationByUserAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        // given
        given(accountRepository.existsByUsername("username"))
                .willReturn(true);

        // then
        assertThrows(UserAlreadyExistsException.class, () -> authService.validate(request));
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 불일치하여 유효성 검증 실패")
    public void failedValidationByPasswordConfirmIncorrect() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("passworld");
        request.setNickname("nickname");

        assertThrows(PasswordConfirmIncorrectException.class, () -> authService.validate(request));
    }

    // 회원가입

    @Test
    @DisplayName("회원가입 성공")
    public void successfulRegister() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        authService.register(request);

        assertDoesNotThrow(() -> authService.register(request)); // 회원가입 성공 시 어떤 오류도 던져지지 않음
    }

    @Test
    @DisplayName("이미 유저 존재하여 회원가입 실패")
    public void failedRegisterByUserAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        given(accountRepository.existsByUsername("username"))
                .willReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 불일치하여 회원가입 실패")
    public void failedRegisterByIncorrectPasswordConfirm() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("passworld");
        request.setNickname("nickname");

        assertThrows(PasswordConfirmIncorrectException.class, () -> authService.register(request));
    }
}
