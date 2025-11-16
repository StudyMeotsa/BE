package com.example.growingstudy.auth.enums;

import lombok.Getter;

@Getter
public enum RegisterFailedType {

    PASSWORD_CONFIRM("패스워드와 패스워드 확인이 일치하지 않습니다."),
    USERNAME_NOT_UNIQUE("이미 해당 username으로 등록된 사용자가 존재합니다.");

    private final String message;

    RegisterFailedType(String message) {
        this.message = message;
    }
}
