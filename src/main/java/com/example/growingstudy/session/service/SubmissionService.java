package com.example.growingstudy.session.service;

import com.example.growingstudy.session.dto.SubmissionCreateDto;
import com.example.growingstudy.session.dto.SubmissionResponseDto;
import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Submission;
import com.example.growingstudy.session.repository.ChecklistRepository;
import com.example.growingstudy.session.repository.SubmissionRepository;
import com.example.growingstudy.studygroup.entity.GroupMember;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor 
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ChecklistRepository checklistRepository;
    private final EntityManager em;

    @Transactional
    public Long createSubmission(Long checklistId, SubmissionCreateDto dto) {
        // 1. 체크리스트 조회
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("해당 체크리스트가 없습니다. ID: " + checklistId));

        // 2. 제출자(GroupMember) 조회
        GroupMember submitter = em.find(GroupMember.class, dto.getMemberId());
        if (submitter == null) {
            throw new IllegalArgumentException("해당 멤버를 찾을 수 없습니다. ID: " + dto.getMemberId());
        }

        // 3. 빌더를 이용한 엔티티 생성
        Submission submission = Submission.builder()
                .content(dto.getContent())
                .imagePath(dto.getImagePath())
                .checklist(checklist)
                .submitter(submitter)
                .build();

        return submissionRepository.save(submission).getId();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getSubmissionsByChecklist(Long checklistId) {
        // 엔티티 리스트를 DTO 리스트로 변환하여 반환 (순환 참조 방지)
        return submissionRepository.findByChecklistId(checklistId).stream()
                .map(SubmissionResponseDto::from)
                .collect(Collectors.toList());
    }
}
