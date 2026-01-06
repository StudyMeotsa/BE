package com.example.growingstudy.studygroup.dto;

import java.time.LocalDateTime;

public record CreateGroupRequest(
        String name,
        LocalDateTime startDay,
        Integer weekSession,
        Integer totalWeek,
        Integer maxMember,
        Integer sessionHour,
        String description
) {}
