package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Session;

import java.time.LocalDateTime;

public record SessionInfoRequest(
        Integer sessionOrder,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime
){}
