package com.example.growingstudy.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GroupListResponse {
    private Long groupId;
    private String name;
    private LocalDate startDay;
    private LocalDate endDay;
    private Integer weekSession;
    private Integer totalSessions;
    private Integer sessionHour;
    private Long currentMember;
    private Integer maxMember;
    private Long sessionId;
    private String coffee;
    private Integer coffeeLevel;
}
