package com.example.growingstudy.studygroup.dto;

import java.time.LocalDate;

public record GroupListInfoResponse(
        Long groupId,
        Long sessionId,
        String name,
        LocalDate startDay,
        LocalDate endDay,
        Integer weekSession,
        Integer totalSessions,
        Integer studyTimeAim,
        Long currentMember,
        Integer maxMember,
        Integer sessionOrder,
        String coffeeImagePath
){}
