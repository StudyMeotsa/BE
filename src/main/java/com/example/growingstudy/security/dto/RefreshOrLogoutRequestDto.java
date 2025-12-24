package com.example.growingstudy.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshOrLogoutRequestDto {

    private String refreshToken;
}
