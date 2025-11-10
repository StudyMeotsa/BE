package com.checklist.dto;

import com.checklist.entity.Checklist;
import lombok.*;

@Getter
@AllArgsConstructor
public class ChecklistResponse {
    private Long id;          // 항목 ID
    private String content;   // 할 일 내용
    private boolean completed;// 완료 여부

    public static ChecklistResponse from(Checklist checklist) {
        return new ChecklistResponse(
                checklist.getId(),
                checklist.getContent(),
                checklist.isCompleted()
        );
    }
}