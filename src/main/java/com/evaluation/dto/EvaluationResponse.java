package com.evaluation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Long id;
    private int score;
    private String comment;
    private Long checklistId;
}