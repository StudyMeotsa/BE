package com.checklist.dto;

import com.checklist.entity.Checklist;
import lombok.*;

@Getter
@AllArgsConstructor
public class ChecklistResponse {
    private Long id; 
    private String content; 
    private String description;
    private boolean completed;
    
    private Long participantCount; // 명수 카운트
    private Long durationMinutes;  // 세션 경과 시간 (분 단위)

    public static ChecklistResponse from(Checklist checklist, Long count, Long duration) {
        return new ChecklistResponse(
                checklist.getId(),
                checklist.getContent(),
                checklist.getDescription(),
                checklist.isCompleted(),
                count, 
                duration
        );
    }
}