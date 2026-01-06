package com.example.growingstudy.auth.dto;

import com.example.growingstudy.auth.constraint.PasswordConfirmConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordConfirmConstraint // DB가 필요한 중복 확인은 서비스에서 수행
public class RegisterRequestDto {

    // 이메일, 비밀번호, 이름, 성별

    @NotBlank @Email private String email;
    @NotBlank private String password;
    @NotBlank private String passwordConfirm;
    @NotBlank private String name;

    // 성별: M 또는 F
    @NotBlank @Pattern(regexp = "^[MF]$", message = "성별은 M 또는 F여야 합니다") private String sex;
}
