package com.example.growingstudy.session.dto;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class SubmissionCreateDto {
    private Long memberId;
    private String content;
    private String imagePath;
}