package com.example.growingstudy.session.dto;

import com.example.growingstudy.coffee.dto.GroupCoffeeProgressDto;

import java.util.List;

public record ChecklistOverviewResponse(
        SessionInfoResponse session,
        GroupCoffeeProgressDto coffee,
        List<ChecklistStatusDto> checklists
) {
}
