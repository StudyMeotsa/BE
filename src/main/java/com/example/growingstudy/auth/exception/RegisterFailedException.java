package com.example.growingstudy.auth.exception;

import com.example.growingstudy.auth.enums.RegisterFailedType;
import lombok.Getter;

@Getter
public class RegisterFailedException extends RuntimeException {

    private final RegisterFailedType type; // 회원가입 실패 원인

    public RegisterFailedException(RegisterFailedType type) {
        super(type.getMessage());
        this.type = type;
    }
}
