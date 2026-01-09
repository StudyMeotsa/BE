package com.example.growingstudy.session.service;

import com.example.growingstudy.session.dto.*;
import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.repository.ChecklistRepository;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.session.repository.SubmissionRepository;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import com.example.growingstudy.studygroup.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final SubmissionRepository submissionRepository;
    private final SessionRepository sessionRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;  // 멤버검증용

    /**
     * 체크리스트 생성
     */
    public void createChecklist(Long accountId, Long groupId, Long sessionId, ChecklistInfoDto request) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

        Session session = sessionRepository.findByIdAndGroup_Id(sessionId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 존재하지 않습니다."));

        Checklist checklist = Checklist.create(
                request.title(),
                request.description(),
                session
        );

        checklistRepository.save(checklist);
    }

    /**
     * 특정 세션에 속한 모든 체크리스트 조회
     */
    public ChecklistOverviewResponse getChecklistsBySession(Long accountId, Long groupId, Long sessionId) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 존재하지 않습니다."));

        // 1) 체크리스트 id, content
        List<ChecklistsPerSessionView> checklists = checklistRepository.findProjectedBySessionId(sessionId);

        // 1-1) 체크리스트 id
        List<Long> checklistIds = checklists.stream().map(ChecklistsPerSessionView::getId).toList();

        // *체크리스트 없을 경우
        if (checklistIds.isEmpty()) {
            return new ChecklistOverviewResponse(SessionInfoResponse.from(session), List.of());
        }

        // 2) maxMember
        Integer maxMember = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 그룹이 없습니다."))
                .getMaxMember();

        // 3) doneMember
        Map<Long, Integer> doneMembers = submissionRepository.doneMemberCountByChecklistIds(checklistIds)
                .stream()
                .collect(Collectors.toMap(
                        DoneMemberCountView::getChecklistId,
                        DoneMemberCountView::getDoneMember
                ));
        // 4) mySubmissions
        Set<Long> mySubmissions = new HashSet<>(
                submissionRepository.findMySubmissions(checklistIds, accountId)
        );

        // 5) 합치기
        List<ChecklistStatusDto> checklistsDto = checklists
                .stream()
                .map(ch -> new ChecklistStatusDto(
                        ch.getId(),
                        ch.getTitle(),
                        doneMembers.getOrDefault(ch.getId(), 0),
                        maxMember,
                        mySubmissions.contains(ch.getId())
                )).toList();

        return new ChecklistOverviewResponse(
                SessionInfoResponse.from(session),
                checklistsDto
                );

        //커피 추가 시

//        return new ChecklistOverviewResponse(
//                SessionInfoResponse.from(session),
//                GroupCoffeeProgressDto,
//                checklistsDto
//        );
    }
}
