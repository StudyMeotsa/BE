package com.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChecklistRequest {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;   // 할 일 내용
}