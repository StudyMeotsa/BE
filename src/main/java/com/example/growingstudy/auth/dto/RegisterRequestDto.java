package com.example.growingstudy.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    // 이메일, 비밀번호, 닉네임

    private String username;
    private String password;
    private String passwordConfirm;
    private String nickname;
}
