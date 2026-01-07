package com.example.growingstudy.groupsub.dto;

import java.time.LocalDateTime;

public record TimeLogsResponse(
        Long id,
        Integer time,
        LocalDateTime createdAt
) {
}
