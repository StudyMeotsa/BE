package com.checklist.repository;

import com.checklist.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    // N+1 문제 해결 및 명수 카운트 최적화 쿼리
    @Query("SELECT s.checklist.id, COUNT(s.id) FROM Submission s WHERE s.checklist.id IN :checklistIds GROUP BY s.checklist.id")
    List<Object[]> countSubmissionsByChecklistIds(@Param("checklistIds") List<Long> checklistIds); 
    
    List<Submission> findByChecklistId(Long checklistId);
    
    Optional<Submission> findByChecklistIdAndSubmitterId(Long checklistId, Long submitterId);
}