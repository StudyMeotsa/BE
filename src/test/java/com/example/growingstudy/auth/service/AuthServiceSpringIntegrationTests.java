package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.exception.PasswordConfirmIncorrectException;
import com.example.growingstudy.auth.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class AuthServiceSpringIntegrationTests {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    public void successfulRegister() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        assertDoesNotThrow(() -> authService.register(request)); // 회원가입 성공하므로 예외가 던져지지 않음
    }

    @Test
    @DisplayName("이미 가입한 유저 존재하여 두번째 회원가입 실패")
    public void failedSecondRegisterByUserAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        assertDoesNotThrow(() -> authService.register(request)); // 첫번째 회원가입: 성공

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인 불일치하여 회원가입 실패")
    public void failedRegisterByIncorrectPasswordConfirm() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("passworld");
        request.setNickname("nickname");

        assertThrows(PasswordConfirmIncorrectException.class, () -> authService.register(request));
    }
}
