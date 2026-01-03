package com.example.growingstudy.session.repository;

import com.example.growingstudy.session.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    long countByChecklistId(Long checklistId);
    boolean existsByChecklistId(Long checklistId);
    List<Submission> findByChecklistId(Long checklistId);
}