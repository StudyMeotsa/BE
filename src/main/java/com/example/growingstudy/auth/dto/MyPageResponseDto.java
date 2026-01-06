package com.example.growingstudy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageResponseDto {

    @NotBlank private String name;
    @NotBlank @Pattern(regexp = "^[MF]$", message = "성별은 M 또는 F여야 합니다") private String sex;
    @NotBlank @Email private String email;
}
