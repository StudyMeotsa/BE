package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistCreateDto {
    private String content;
    private String description;
    private Long groupId;

    public Checklist toEntity(StudyGroup group) {
        Checklist checklist = new Checklist();
        checklist.setContent(this.content);
        checklist.setDescription(this.description);
        checklist.setCompleted(false); // 초기값 설정

        checklist.setGroup(group);
        return checklist;
    }
}
