package com.example.growingstudy.auth.controller;

import com.example.growingstudy.auth.controller.AuthController;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class AuthControllerSpringIntegrationTests {

    @Autowired
    private AuthController authController;

    @Test
    @DisplayName("회원가입 성공")
    public void successfulRegister() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        // when
        ResponseEntity<?> response = authController.register(request);

        // then
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    @DisplayName("이미 유저 존재하여 2번째 회원가입 실패")
    public void failedSecondRegisterByUserAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");
        ResponseEntity<?> firstResponse = authController.register(request); // 회원가입 진행
        assertEquals(HttpStatus.ACCEPTED, firstResponse.getStatusCode()); // 아무 것도 없으므로 성공해야 함

        // when
        ResponseEntity<?> secondResponse = authController.register(request); // 같은 내용으로 회원가입 진행

        // then
        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode());
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 불일치하여 회원가입 실패")
    public void failedRegisterByIncorrectPasswordConfirm() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("passworld");
        request.setNickname("nickname");

        // when
        ResponseEntity<?> response = authController.register(request);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
