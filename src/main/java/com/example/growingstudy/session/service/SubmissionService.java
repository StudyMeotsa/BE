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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor 
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ChecklistRepository checklistRepository;
    private final S3Service s3Service;
    private final EntityManager em;

    @Transactional
    public Long createSubmission(Long checklistId, SubmissionCreateDto dto, MultipartFile image) throws IOException { // MultipartFile 추가
        // 1. 체크리스트 조회
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("해당 체크리스트가 없습니다. ID: " + checklistId));

        // 2. 제출자(GroupMember) 조회
        GroupMember submitter = em.find(GroupMember.class, dto.getMemberId());
        if (submitter == null) {
            throw new IllegalArgumentException("해당 멤버를 찾을 수 없습니다. ID: " + dto.getMemberId());
        }

        // 3. 이미지 파일이 있으면 S3에 업로드
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            // S3Service에 정의한 메서드명 'uploadFile'로 호출하거나, 
            // S3Service의 메서드명을 'upload'로 바꾸어 사용하세요.
            imageUrl = s3Service.uploadFile(image); 
        }

        // 4. 빌더를 이용한 엔티티 생성
        Submission submission = Submission.builder()
                .content(dto.getContent())
                .imagePath(imageUrl)
                .checklist(checklist)
                .submitter(submitter)
                .build();

        return submissionRepository.save(submission).getId();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getSubmissionsByChecklist(Long checklistId) {
        return submissionRepository.findByChecklistId(checklistId).stream()
                .map(SubmissionResponseDto::from)
                .collect(Collectors.toList());
    }
}
