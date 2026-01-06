package com.example.growingstudy.session.service;

import com.example.growingstudy.session.dto.ChecklistCreateDto;
import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.repository.ChecklistRepository;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.session.repository.SubmissionRepository;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import com.example.growingstudy.studygroup.repository.GroupsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("null")
@Service
@Transactional
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final SessionRepository sessionRepository;
    private final GroupsRepository groupsRepository;
    private final SubmissionRepository submissionRepository;

    /**
     * 새로운 세션 체크리스트 생성
     */
    public Checklist createChecklist(ChecklistCreateDto dto) {
        StudyGroup group = groupsRepository.findById(dto.getGroupId()).orElseThrow();
        Session session = sessionRepository.findById(dto.getSessionId()).orElseThrow();
        
        return checklistRepository.save(new Checklist(dto.getContent(), dto.getDescription(), group, session));
    }

    /**
     * 체크리스트 수동으로 완료 처리
     */
    public void markComplete(Long id) {
        checklistRepository.findById(id).ifPresent(c -> c.setCompleted(true));
    }

    /**
     * 해당 체크리스트의 전체 스터디원 대비 제출률 계산.
     * 계산식: (현재 제출 인원 / 그룹 최대 인원) * 100
     */
    public int calculateProgressRate(Long checklistId) {
        // 1. 체크리스트 정보 조회
        Checklist checklist = checklistRepository.findById(checklistId).orElseThrow();
        
        // 2. 현재까지 제출된 인증물 개수 카운트
        long count = submissionRepository.countByChecklistId(checklistId);
        
        // 3. 그룹 설정 정보에서 최대 인원수(maxMember) 추출
        Integer max = checklist.getGroup().getMaxMember();
        
        // 4. 0으로 나누기 방지 및 백분율 계산
        return (max == null || max == 0) ? 0 : (int)((double)count / max * 100);
    }
}