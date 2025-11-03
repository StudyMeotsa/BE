package com.evaluation.service;

import com.evaluation.dto.EvaluationRequest;
import com.evaluation.dto.EvaluationResponse;
import com.checklist.entity.Checklist;
import com.evaluation.entity.evaluation;
import com.checklist.repository.ChecklistRepository;
import com.evaluation.repository.EvaluationRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ChecklistRepository checklistRepository;

    public EvaluationService(EvaluationRepository evaluationRepository, ChecklistRepository checklistRepository) {
        this.evaluationRepository = evaluationRepository;
        this.checklistRepository = checklistRepository;
    }

    public void createEvaluation(EvaluationRequest request) {
        Checklist checklist = checklistRepository.findById(request.getChecklistId()).orElseThrow();

        evaluation evaluation = new evaluation();
        evaluation.setScore(request.getScore());
        evaluation.setComment(request.getComment());
        evaluation.setUserId(request.getUserId());
        evaluation.setChecklist(checklist);

        evaluationRepository.save(evaluation);
    }

    public List<EvaluationResponse> getEvaluationsByUser(Long userId) {
        return evaluationRepository.findByUserId(userId).stream()
                .map(e -> new EvaluationResponse(e.getId(), e.getScore(), e.getComment(), e.getChecklist().getId()))
                .collect(Collectors.toList());
    }
}