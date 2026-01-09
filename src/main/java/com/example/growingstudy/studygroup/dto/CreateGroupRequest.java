package com.example.growingstudy.studygroup.dto;

import java.time.LocalDate;

public record CreateGroupRequest(
        String name,
        LocalDate startDay,
        Integer weekSession,
        Integer totalWeek,
        Integer maxMember,
        Integer studyTimeAim,
        String description
) {}
