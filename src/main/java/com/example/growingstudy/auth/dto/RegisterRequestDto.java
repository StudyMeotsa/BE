package com.example.growingstudy.auth.dto;

import com.example.growingstudy.auth.constraint.PasswordConfirmConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordConfirmConstraint // DB가 필요한 중복 확인은 서비스에서 수행
public class RegisterRequestDto {

    // 이메일, 비밀번호, 닉네임
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String passwordConfirm;
    @NotBlank private String nickname;
}
