package com.evaluation.dto;

import com.evaluation.entity.Evaluation;
import lombok.*;

@Getter
@AllArgsConstructor
public class EvaluationResponse {
    private Long id;
    private int score;
    private String comment;
    private Long checklistId;

    public static EvaluationResponse from(Evaluation evaluation) {
        return new EvaluationResponse(
                evaluation.getId(),
                evaluation.getScore(),
                evaluation.getComment(),
                evaluation.getChecklist().getId()
        );
    }
}