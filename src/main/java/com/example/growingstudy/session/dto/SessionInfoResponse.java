package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Session;

import java.time.LocalDateTime;

public record SessionInfoResponse(
        Long id,
        Integer sessionOrder,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static SessionInfoResponse from(Session session) {
        return new SessionInfoResponse(
                session.getId(),
                session.getSessionOrder(),
                session.getTitle(),
                session.getStartTime(),
                session.getEndTime()
        );
    }
}
