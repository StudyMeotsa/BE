package com.example.growingstudy.auth.advice;

import com.example.growingstudy.auth.dto.RegisterErrorDto;
import com.example.growingstudy.auth.exception.RegisterFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RegisterExceptionControllerAdvice {

    @ExceptionHandler(RegisterFailedException.class)
    public ResponseEntity<RegisterErrorDto> handleRegisterFailedException(RegisterFailedException e) {
        RegisterErrorDto response = new RegisterErrorDto();
        response.setError(e.getMessage());

        return ResponseEntity.badRequest().body(response);
    }
}
