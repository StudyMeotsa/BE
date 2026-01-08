package com.example.growingstudy.session.dto;

import java.util.List;

public record ChecklistOverviewResponse(
        SessionInfoResponse session,
//        GroupCoffeeProgressDto coffee,    //coffee.dto에 넣어뒀음
        List<ChecklistStatusDto> checklists
) {
}
