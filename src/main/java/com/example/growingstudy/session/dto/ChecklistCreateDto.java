package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Session;
import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class ChecklistCreateDto {
    private String content;     // 할 일 제목
    private String description; // 상세 설명
    private Long sessionId;     // 세션 ID (ERD 기반 FK)

    public Checklist toEntity(Session session) {
        return Checklist.builder()
                .content(this.content)
                .description(this.description)
                .session(session)
                .build();
    }
}