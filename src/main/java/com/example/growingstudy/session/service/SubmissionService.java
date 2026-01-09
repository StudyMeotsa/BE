package com.example.growingstudy.session.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Submission;
import com.example.growingstudy.session.repository.ChecklistRepository;
import com.example.growingstudy.session.repository.SubmissionRepository;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionService {

    private final AccountRepository accountRepository;
    private final SubmissionRepository submissionRepository;
    private final ChecklistRepository checklistRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EntityManager em;

    public void createSubmission(Long accountId, Long groupId, Long sessionId, Long checklistId, String content, MultipartFile file) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("해당 체크리스트를 찾을 수 없습니다."));

        String imagePath = null;
        // Todo: 변환 로직 추가

        Submission submission = Submission.create(content, imagePath, checklist, account);

        submissionRepository.save(submission);
    }

//    @Transactional(readOnly = true)
//    public List<SubmissionResponseDto> getSubmissionsByChecklist(Long checklistId) {
//        // 엔티티 리스트를 DTO 리스트로 변환하여 반환 (순환 참조 방지)
//        return submissionRepository.findByChecklistId(checklistId).stream()
//                .map(SubmissionResponseDto::from)
//                .collect(Collectors.toList());
//    }

//    public List<SubmissionViewResponse> get
}
