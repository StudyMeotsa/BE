package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class ChecklistCreateDto {
    private String content;     // 할 일 제목
    private String description; // 상세 설명
    private Long sessionId;     // 세션 ID

    public Checklist toEntity(StudyGroup group, Session session) {
        return new Checklist(this.content, this.description, session);
    }
}