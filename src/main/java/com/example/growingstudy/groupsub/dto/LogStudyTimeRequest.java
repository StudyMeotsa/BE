package com.example.growingstudy.groupsub.dto;

import java.time.LocalDateTime;

public record LogStudyTimeRequest(
        Integer time,
        LocalDateTime createdAt
) {
}
