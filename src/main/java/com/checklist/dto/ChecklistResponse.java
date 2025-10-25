package com.checklist.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistResponse {
    private Long id;          // 항목 ID
    private String content;   // 할 일 내용
    private boolean completed;// 완료 여부
}