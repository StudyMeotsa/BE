package com.example.growingstudy.controller;

import com.example.growingstudy.dto.RegisterErrorDto;
import com.example.growingstudy.dto.RegisterRequestDto;
import com.example.growingstudy.exception.RegisterFailException;
import com.example.growingstudy.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
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
}
