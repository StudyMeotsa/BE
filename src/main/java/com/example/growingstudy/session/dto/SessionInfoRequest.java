package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Session;

import java.time.LocalDate;

public record SessionInfoRequest(
        Integer sessionOrder,
        String title,
        LocalDate startTime,
        LocalDate endTime
){}
