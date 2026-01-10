package com.example.growingstudy.session.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.session.dto.ChecklistInfoDto;
import com.example.growingstudy.session.dto.SessionInfoResponse;
import com.example.growingstudy.session.dto.SubmissionInfoDto;
import com.example.growingstudy.session.dto.SubmissionOverviewResponse;
import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.entity.Submission;
import com.example.growingstudy.session.repository.ChecklistRepository;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.session.repository.SubmissionRepository;
import com.example.growingstudy.studygroup.entity.GroupMember;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionService {

    private final AccountRepository accountRepository;
    private final SubmissionRepository submissionRepository;
    private final ChecklistRepository checklistRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SessionRepository sessionRepository;

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

    @Transactional(readOnly = true)
    public SubmissionOverviewResponse getSubmissionOverview(Long accountId, Long groupId, Long sessionId, Long checklistId) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 존재하지 않습니다."));

        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("체크리스트가 존재하지 않습니다."));

        List<SubmissionInfoDto> submissions = submissionRepository.findMyVerifiedSubmissionsIdByChecklist_Id(checklistId);

        return new SubmissionOverviewResponse(
                SessionInfoResponse.from(session),
                new ChecklistInfoDto(checklist.getTitle(), checklist.getDescription()),
                submissions);
    }

    public void verifySubmission(Long accountId, Long groupId, Long sessionId, Long checklistId, Long submissionId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        GroupMember groupMember = groupMemberRepository.findByAccountIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        if (!groupMember.getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("해당 그룹의 방장이 아닙니다."); // 추후에 예외 타입 수정
        }

        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("해당 체크리스트를 찾을 수 없습니다."));

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제출 데이터를 찾을 수 없습니다."));

        submission.setIsVerifiedTrue();
        submissionRepository.save(submission);
    }
}
