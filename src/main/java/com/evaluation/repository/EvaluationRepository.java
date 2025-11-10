package com.evaluation.repository;

import com.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByUserId(Long userId);
    Optional<Evaluation> findByChecklistId(Long checklistId);
}