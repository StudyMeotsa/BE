package com.evaluation.controller;

import com.evaluation.dto.EvaluationRequest;
import com.evaluation.dto.EvaluationResponse;
import com.evaluation.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService service;

    public EvaluationController(EvaluationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> createEvaluation(@RequestBody EvaluationRequest request) {
        service.createEvaluation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> getEvaluations(@RequestParam Long userId) {
        return ResponseEntity.ok(service.getEvaluationsByUser(userId));
    }
}