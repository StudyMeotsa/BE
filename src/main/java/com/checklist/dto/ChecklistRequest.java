package com.checklist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChecklistRequest {
    private String content;   // 할 일 내용
    private Long userId;      // 사용자 ID
}