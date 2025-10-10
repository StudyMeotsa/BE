package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
}
