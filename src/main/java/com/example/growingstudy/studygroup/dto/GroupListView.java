package com.example.growingstudy.studygroup.dto;

import java.time.LocalDate;

public interface GroupListView {
    Long getGroupId();
    String getName();
    LocalDate getStartDay();
    LocalDate getEndDay();
    Integer getWeekSession();
    Integer getTotalSessions();
    Integer getStudyTimeAim();
    Long getCurrentMember();
    Integer getMaxMember();
    Long getSessionId();
    String getCoffee();
    Integer getCoffeeLevel();
}
