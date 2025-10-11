package com.example.growingstudy.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

    private String username;
    private String password;
    private String passwordConfirm;
    private String nickname;
}
