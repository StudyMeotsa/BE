package com.example.growingstudy.session.dto;

import com.example.growingstudy.session.entity.Submission;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponseDto {
    private Long submissionId;
    private String content;
    private String imagePath;
    private boolean isVerified;
    private LocalDateTime submittedAt;
    private Long submitterId;
    private String submitterNickname;

    public static SubmissionResponseDto from(Submission entity) {
        return SubmissionResponseDto.builder()
                .submissionId(entity.getId())
                .content(entity.getContent())
                .imagePath(entity.getImagePath())
                .isVerified(entity.isVerified())
                .submittedAt(entity.getSubmittedAt())
                .submitterId(entity.getSubmitter().getId())
                .submitterNickname(entity.getSubmitter().getAccount().getName())
                .build();
    }
}