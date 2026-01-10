package com.example.growingstudy.session.dto;

public record SubmissionInfoDto(
        Long id,
        String content,
        String imagePath,
        boolean isVerified,
        java.sql.Timestamp submittedAt,
        String username
) {
}
