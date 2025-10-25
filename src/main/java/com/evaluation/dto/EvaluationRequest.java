package com.evaluation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationRequest {
    private int score;
    private String comment;
    private Long userId;
    private Long checklistId;
}