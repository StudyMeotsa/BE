package com.checklist.dto;

import com.checklist.entity.Submission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SubmissionResponse {
    private Long submissionId;
    private Long submitterId;
    private String submitterUsername; 
    private String submissionData;
    private LocalDateTime submittedAt;

    public static SubmissionResponse from(Submission submission) {
        return new SubmissionResponse(
                submission.getId(),
                submission.getSubmitter().getId(),
                submission.getSubmitter().getUsername(), // User 엔티티에 getUsername() 가정
                submission.getSubmissionData(),
                submission.getSubmittedAt()
        );
    }
}
