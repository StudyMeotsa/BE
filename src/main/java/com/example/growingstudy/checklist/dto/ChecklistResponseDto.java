package com.example.growingstudy.checklist.dto;

import com.example.growingstudy.checklist.entity.Checklist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistResponseDto {
    private Long id;
    private String content;
    private String description;
    private Long groupId;

    public static ChecklistResponseDto from(Checklist entity) {
        ChecklistResponseDto dto = new ChecklistResponseDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setDescription(entity.getDescription());

        // Group 객체가 로드되었는지 확인하고 ID만 추출하여 설정
        if (entity.getGroup() != null) {
            dto.setGroupId(entity.getGroup().getId());
        }
        return dto;
    }
}
