package com.example.growingstudy.studyGroup.dto;

import java.time.LocalDate;

public interface GroupsListView {
    Long getGroupId();
    String getName();
    LocalDate getStartDay();
    LocalDate getEndDay();
    Integer getWeekSession();
    Integer getTotalSessions();
    Integer getSessionHour();
    Long getCurrentMember();
    Integer getMaxMember();
    Long getSessionId();
    String getCoffee();
    Integer getCoffeeLevel();
}
