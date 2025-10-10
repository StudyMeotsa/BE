package com.example.growingstudy.auth.controller;

import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.exception.UserAlreadyExistsException;
import com.example.growingstudy.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTests {

    @Mock
    private AuthService authService;

    @InjectMocks
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
        verify(authService).register(request); // 회원가입 호출 여부
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    @DisplayName("이미 유저 존재하여 회원가입 실패")
    public void failedRegisterByUserAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setNickname("nickname");

        // given
        willThrow(new UserAlreadyExistsException())
                .given(authService).register(request);

        // when
        ResponseEntity<?> response = authController.register(request);

        // then
        verify(authService).register(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 불일치하여 회원가입 실패")
    public void failedRegisterByIncorrectPasswordConfirm() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordConfirm("passworld");
        request.setNickname("nickname");

        // given
        willThrow(new UserAlreadyExistsException())
                .given(authService).register(request);

        // when
        ResponseEntity<?> response = authController.register(request);

        // then
        verify(authService).register(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
