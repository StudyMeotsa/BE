package com.example.growingstudy.session.dto;

import java.time.LocalDateTime;

public record SubmissionInfoDto(
        Long id,
        String content,
        String imagePath,
        boolean isVerified,
        java.sql.Timestamp submittedAt,
        String username
) {
}
