package com.example.growingstudy.session.dto;

import java.util.List;

public record SubmissionOverviewResponse(
        SessionInfoResponse session,
        ChecklistInfoDto checklist,
        List<SubmissionInfoDto> submissions
) {
}
