package com.example.growingstudy.auth.controller;

import com.example.growingstudy.auth.dto.JwtResponseDto;
import com.example.growingstudy.auth.dto.RefreshOrLogoutRequestDto;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestAttribute("username") String username) {
        logger.debug("로그인 후 토큰 발급 처리 시작");
        JwtResponseDto body = authService.generateJwtToken(username);
        logger.debug("로그인 후 토큰 발급 처리 성공");
        logger.info("유저 {}가 로그인", username);
        return ResponseEntity.accepted().body(body);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Validated @RequestBody RegisterRequestDto request) {
        logger.debug("회원가입 요청 처리 시작");

        authService.register(request);
        logger.info("유저 {}가 회원가입", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refreshTokens(@RequestBody RefreshOrLogoutRequestDto request) {
        logger.debug("토큰 재발급 처리 시작");
        String refreshString = request.getRefreshToken();
        JwtResponseDto body = authService.refreshTokens(refreshString);
        logger.debug("토큰 재발급 처리 성공");
        return ResponseEntity.accepted().body(body);
    }

    // 로그아웃 엔드포인트: 실제로 이 메소드가 호출되진 않으나, 일관성 및 Swagger 위해 추가
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshOrLogoutRequestDto request) {
        return ResponseEntity.accepted().build();
    }
}
