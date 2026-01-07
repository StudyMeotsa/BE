package com.example.growingstudy.studygroup.dto;

import com.example.growingstudy.studygroup.entity.StudyGroup;

public record GroupInfoResponse(
        Long groupId,
        String name,
        Integer weekSession,
        Integer totalWeek,
        Integer studyTimeAim,
        Integer maxMember,
        String description,
        String code
) {

    public static GroupInfoResponse from(StudyGroup group) {
        return new GroupInfoResponse(
                group.getId(),
                group.getName(),
                group.getWeekSession(),
                group.getTotalWeek(),
                group.getStudyTimeAim(),
                group.getMaxMember(),
                group.getDescription(),
                group.getCode()
        );
    }
}
