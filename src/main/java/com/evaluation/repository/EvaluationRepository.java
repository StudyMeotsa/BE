package com.evaluation.repository;

import com.evaluation.entity.evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<evaluation, Long> {
    List<evaluation> findByUserId(Long userId);
}