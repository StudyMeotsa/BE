package com.checklist.service;

import com.checklist.entity.Checklist;
import com.checklist.repository.ChecklistRepository;
import com.submission.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final SubmissionRepository submissionRepository;

    public ChecklistService(ChecklistRepository checklistRepository, SubmissionRepository submissionRepository) {
        this.checklistRepository = checklistRepository;
        this.submissionRepository = submissionRepository;
    }

    public Checklist createChecklist(Checklist checklist) {
        return checklistRepository.save(checklist);
    }

    public List<Checklist> getChecklists(Long groupId) {
        return checklistRepository.findByGroupId(groupId);
    }

    public void markComplete(Long id) {
        Checklist checklist = checklistRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Checklist not found"));
        checklist.complete();
    }

    public void startSession(Long id) {
        Checklist checklist = checklistRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Checklist not found"));
        checklist.startSession();
    }

    public void endSession(Long id) {
        Checklist checklist = checklistRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Checklist not found"));
        checklist.endSession();
    }

    // 분 단위로 타이머 값 변환
    public Long calculateDurationMinutes(Long id) {
        Checklist checklist = checklistRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Checklist not found"));

        LocalDateTime start = checklist.getStartTime();
        LocalDateTime end = checklist.getEndTime();

        if (start == null) return 0L;
        LocalDateTime actualEnd = (end != null) ? end : LocalDateTime.now();
        return Duration.between(start, actualEnd).toMinutes();
    }

    // MaxMember 값을 가져와 스터디 전체 목표 달성률 계산 
    public int calculateProgressRate(Long checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
            .orElseThrow(() -> new IllegalStateException("Checklist not found"));

        long submittedCount = submissionRepository.countByChecklistId(checklistId);
        int totalMembers = Integer.parseInt(checklist.getGroup().getMaxMember());

        if (totalMembers == 0) return 0;
        return (int) ((double) submittedCount / totalMembers * 100);
    }
}