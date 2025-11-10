package com.evaluation.controller;

import com.evaluation.dto.EvaluationRequest;
import com.evaluation.dto.EvaluationResponse;
import com.evaluation.entity.Evaluation;
import com.evaluation.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService service;

    public EvaluationController(EvaluationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> createEvaluation(@Valid @RequestBody EvaluationRequest request, @AuthenticationPrincipal Long userId) {
        Evaluation evaluation = service.createEvaluation(request, userId);
        return ResponseEntity.created(URI.create("/api/evaluations/" + evaluation.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> getMyEvaluations(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(service.getEvaluationsByUser(userId));
    }
}