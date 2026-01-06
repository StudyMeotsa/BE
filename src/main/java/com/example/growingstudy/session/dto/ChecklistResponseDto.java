package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Checklist;
import lombok.*;
import java.time.LocalDateTime;

@Getter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class ChecklistResponseDto {
    private Long checklist_id;
    private Long session_id;
    private String content;
    private String description;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private boolean completed;

    public static ChecklistResponseDto from(Checklist entity) {
        return ChecklistResponseDto.builder()
                .checklist_id(entity.getId())
                .session_id(entity.getSession().getId()) 
                .content(entity.getContent())
                .description(entity.getDescription())
                .start_time(entity.getSession().getStartTime())
                .end_time(entity.getSession().getEndTime())
                .completed(entity.isCompleted())
                .build();
    }
}