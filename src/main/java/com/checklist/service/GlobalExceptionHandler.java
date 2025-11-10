package com.checklist.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        // 리소스를 찾지 못했을 때 404 Not Found 반환
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        // 권한 문제나 중복 평가 등 비즈니스 규칙 위반 시 409 Conflict 반환
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // 다른 예외 핸들러들을 여기에 추가할 수 있습니다.
}