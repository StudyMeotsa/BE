//package com.example.growingstudy.session.repository;
//
//import com.example.growingstudy.session.entity.Submission;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface SubmissionRepository extends JpaRepository<Submission, Long> {
//    // 체크리스트별 제출 총 개수 (진행률 계산용)
//    long countByChecklistId(Long checklistId);
//    // 중복 제출 확인
//    boolean existsByChecklistId(Long checklistId);
//    // 특정 체크리스트의 모든 제출물 조회
//    List<Submission> findByChecklistId(Long checklistId);
//}