package com.example.growingstudy.session.dto;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class SubmissionCreateDto {
    private Long memberId;    // submitter_id로 변환될 값
    private String content;   // VARCHAR(2000) 대응
    private String imagePath; // VARCHAR(255) 대응
}