package com.example.growingstudy.studygroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinGroupRequest(
        @NotBlank
        @Size(min = 8, max = 8)
        String code
) {
}
