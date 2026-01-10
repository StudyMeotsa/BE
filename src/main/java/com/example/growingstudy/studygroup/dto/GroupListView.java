package com.example.growingstudy.studygroup.dto;

import java.time.LocalDate;

public interface GroupListView {
    Long getGroupId();
    Long getSessionId();
    String getName();
    LocalDate getStartDay();
    LocalDate getEndDay();
    Integer getWeekSession();
    Integer getTotalSessions();
    Integer getStudyTimeAim();
    Long getCurrentMember();
    Integer getMaxMember();
    Integer getSessionOrder();
    String getCoffeeImagePath();
}
