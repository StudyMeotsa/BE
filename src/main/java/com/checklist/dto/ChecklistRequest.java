package com.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChecklistRequest {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content; 
    
    private String description; // 과제 상세 설명 필드
}