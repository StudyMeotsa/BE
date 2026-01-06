package com.example.growingstudy.session.service;

import com.example.growingstudy.session.dto.ChecklistCreateDto;
import com.example.growingstudy.session.dto.ChecklistResponseDto;
import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.repository.ChecklistRepository;
import com.example.growingstudy.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final SessionRepository sessionRepository;

    /**
     * 체크리스트 생성
     */
    public Long createChecklist(ChecklistCreateDto dto) {
        // 1. 세션 존재 확인
        Session session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 세션을 찾을 수 없습니다. ID: " + dto.getSessionId()));

        // 2. DTO의 toEntity 기능을 사용하여 엔티티 생성
        Checklist checklist = dto.toEntity(session);

        // 3. 저장 및 ID 반환
        return checklistRepository.save(checklist).getId();
    }

    /**
     * 특정 세션에 속한 모든 체크리스트 조회
     */
    @Transactional(readOnly = true)
    public List<ChecklistResponseDto> getChecklistsBySession(Long sessionId) {
        // 엔티티 리스트를 조회하여 DTO로 변환 (제공된 ChecklistResponseDto.from 사용)
        return checklistRepository.findAll().stream()
                .filter(c -> c.getSession().getId().equals(sessionId))
                .map(ChecklistResponseDto::from)
                .collect(Collectors.toList());
    }
}