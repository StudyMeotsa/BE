package com.example.growingstudy.auth.controller;

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

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Validated @RequestBody RegisterRequestDto request) {
        logger.debug("회원가입 요청 처리 시작");

        authService.register(request);
        logger.info("유저 {}가 회원가입", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
