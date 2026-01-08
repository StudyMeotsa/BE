package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Checklist;

public record ChecklistStatusDto(
        Long checklistId,
        String content,
        int doneMember,
        int maxMember,
        boolean mySubmission
) {
}
