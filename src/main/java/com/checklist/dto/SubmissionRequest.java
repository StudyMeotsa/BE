package com.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SubmissionRequest {
    @NotBlank(message = "제출 내용을 입력해주세요.")
    private String submissionData; // 제출 데이터 (텍스트 또는 파일 경로)
}