//package com.example.growingstudy.session.service;
//
//import com.example.growingstudy.session.dto.SubmissionCreateDto;
//import com.example.growingstudy.session.entity.Checklist;
//import com.example.growingstudy.session.entity.Submission;
//import com.example.growingstudy.session.repository.ChecklistRepository;
//import com.example.growingstudy.session.repository.SubmissionRepository;
//import com.example.growingstudy.studygroup.entity.GroupMember;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@SuppressWarnings("all")
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class SubmissionService {
//
//    private final SubmissionRepository submissionRepository;
//    private final ChecklistRepository checklistRepository;
//    private final EntityManager em; // 별도의 Repository가 없는 GroupMember 조회를 위해 사용
//
//    /**
//     * 사용자의 체크리스트 인증 제출물 저장
//     */
//    public Submission createSubmission(Long checklistId, SubmissionCreateDto dto) {
//        // 1. 제출 대상이 되는 체크리스트 존재 여부 확인
//        Checklist checklist = checklistRepository.findById(checklistId)
//                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));
//
//        // 2. DTO의 memberId를 이용해 제출자(GroupMember) 정보 조회
//        GroupMember submitter = em.find(GroupMember.class, dto.getMemberId());
//        if (submitter == null) throw new IllegalArgumentException("Member not found");
//
//        // 3. 인증 제출물 엔티티 생성 및 저장
//        return submissionRepository.save(new Submission(dto.getContent(), dto.getImagePath(), checklist, submitter));
//    }
//
//    /**
//     * 특정 체크리스트에 제출된 모든 인증물 목록 조회
//     */
//    @Transactional(readOnly = true)
//    public List<Submission> getSubmissionsByChecklist(Long checklistId) {
//        return submissionRepository.findByChecklistId(checklistId);
//    }
//}