package com.example.growingstudy.auth.controller;

import com.example.growingstudy.auth.dto.JwtResponseDto;
import com.example.growingstudy.auth.dto.RefreshOrLogoutRequestDto;
import com.example.growingstudy.auth.dto.RegisterErrorDto;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.exception.RegisterFailException;
import com.example.growingstudy.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestAttribute("username") String username) {
        JwtResponseDto body = authService.generateJwtToken(username);
        return ResponseEntity.accepted().body(body);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        logger.info("회원가입 요청 처리 시작");

        try {
            authService.register(request);
        } catch (RegisterFailException e) {
            logger.error("회원가입 중 오류 발생: {}", e.getMessage());
            RegisterErrorDto body = new RegisterErrorDto();
            body.setError(e.getMessage());
            logger.info("에러 DTO에 설정된 오류 메시지: {}", body.getError());
            return ResponseEntity.badRequest().body(body);
        }

        logger.info("회원가입 요청 처리 성공");
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refreshTokens(@RequestBody RefreshOrLogoutRequestDto request) {
        String refreshString = request.getRefreshToken();
        JwtResponseDto body = authService.refreshTokens(refreshString);
        return ResponseEntity.accepted().body(body);
    }

    // 아직 정상 작동 안하는 엔드포인트
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshOrLogoutRequestDto request) {
        String refreshString = request.getRefreshToken();
        authService.logout(refreshString);
        return ResponseEntity.accepted().build();
    }
}
