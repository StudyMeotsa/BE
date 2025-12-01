package com.submission.repository;

import com.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    long countByChecklistId(Long checklistId);
}