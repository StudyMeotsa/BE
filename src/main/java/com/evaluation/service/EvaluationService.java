package com.evaluation.service;

import com.evaluation.dto.EvaluationRequest;
import com.evaluation.dto.EvaluationResponse;
import com.checklist.entity.User;
import com.checklist.entity.Checklist;
import com.evaluation.entity.Evaluation;
import com.checklist.repository.ChecklistRepository;
import com.checklist.repository.UserRepository; // User 리포지토리 import
import com.evaluation.repository.EvaluationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ChecklistRepository checklistRepository;
    private final UserRepository userRepository; // User 리포지토리 주입

    public EvaluationService(EvaluationRepository evaluationRepository, ChecklistRepository checklistRepository, UserRepository userRepository) {
        this.evaluationRepository = evaluationRepository;
        this.checklistRepository = checklistRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Evaluation createEvaluation(EvaluationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        Checklist checklist = checklistRepository.findById(request.getChecklistId())
                .orElseThrow(() -> new NoSuchElementException("Checklist not found with id: " + request.getChecklistId()));

        // 권한 검증: 자신의 체크리스트만 평가할 수 있는지 확인
        if (!checklist.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only evaluate your own checklist.");
        }

        // 중복 평가 방지 로직 추가
        evaluationRepository.findByChecklistId(checklist.getId()).ifPresent(e -> {
            throw new IllegalStateException("This checklist has already been evaluated.");
        });

        Evaluation evaluation = Evaluation.builder()
                .score(request.getScore())
                .comment(request.getComment())
                .user(user)
                .checklist(checklist)
                .build();

        return evaluationRepository.save(evaluation);
    }

    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByUser(Long userId) {
        return evaluationRepository.findByUserId(userId).stream()
                .map(EvaluationResponse::from)
                .collect(Collectors.toList());
    }
}