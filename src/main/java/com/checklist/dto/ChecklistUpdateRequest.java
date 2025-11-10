package com.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChecklistUpdateRequest {
    @NotBlank(message = "내용을 비워둘 수 없습니다.")
    private String content;
}