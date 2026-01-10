package com.example.growingstudy.session.repository;

import com.example.growingstudy.session.dto.DoneMemberCountView;
import com.example.growingstudy.session.dto.SubmissionInfoDto;
import com.example.growingstudy.session.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    // 체크리스트별 제출 총 개수 (진행률 계산용)
    long countByChecklistId(Long checklistId);
    // 중복 제출 확인
    boolean existsByChecklistId(Long checklistId);
    // 특정 체크리스트의 모든 제출물 조회
    List<Submission> findByChecklistId(Long checklistId);

    @Query(value = """
    SELECT s.checklist_id AS checklistId,
           COUNT(*) AS doneMember
    FROM submission s
    WHERE s.checklist_id IN (:checklistIds)
    GROUP BY s.checklist_id
    """, nativeQuery = true)
    List<DoneMemberCountView> doneMemberCountByChecklistIds(List<Long> checklistIds);

    @Query(value = """
    SELECT DISTINCT s.checklist_id
    FROM submission s
    WHERE s.checklist_id IN (:checklistIds)
      AND s.submitter_id = :accountId
      AND s.is_verified = true
    """, nativeQuery = true)
    List<Long> findMySubmissions(List<Long> checklistIds, Long accountId);

    @Query(value = """
    SELECT
      s.id            AS id,
      s.content       AS content,
      s.image_path    AS imagePath,
      s.is_verified   AS isVerified,
      s.submitted_at  AS submittedAt,
      a.name      AS username
    FROM submission s
    JOIN account a ON a.id = s.submitter_id
    WHERE s.checklist_id = :checklistId
""", nativeQuery = true)
    List<SubmissionInfoDto> findMyVerifiedSubmissionsIdByChecklist_Id(Long checklistId);
}
