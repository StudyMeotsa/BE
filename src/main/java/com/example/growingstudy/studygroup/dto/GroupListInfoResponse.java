package com.example.growingstudy.studygroup.dto;

import java.time.LocalDate;

public record GroupListInfoResponse(
        Long groupId,
        String name,
        LocalDate startDay,
        LocalDate endDay,
        Integer weekSession,
        Integer totalSessions,
        Integer studyTimeAim,
        Long currentMember,
        Integer maxMember,
        Long sessionId,
        String coffee,
        Integer coffeeLevel
){}
