package com.example.growingstudy.session.dto;

import java.time.LocalDate;

public record SubmissionInfoDto(
        Long id,
        String content,
        String imagePath,
        boolean isVerified,
        LocalDate submittedAt,
        String username
) {
}
