package com.evaluation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EvaluationRequest {
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다.")
    @Max(value = 5, message = "점수는 5점 이하이어야 합니다.")
    private int score;
    @Size(max = 500, message = "코멘트는 500자를 넘을 수 없습니다.")
    private String comment;
    @NotNull(message = "체크리스트 ID가 필요합니다.")
    private Long checklistId;
}