package com.checklist.service;

import com.checklist.dto.ChecklistRequest;
import com.checklist.dto.ChecklistUpdateRequest;
import com.checklist.dto.ChecklistResponse;
import com.checklist.entity.Checklist;
import com.checklist.entity.User;
import com.checklist.repository.ChecklistRepository;
import com.checklist.repository.UserRepository;
import com.checklist.repository.SubmissionRepository; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ChecklistService {

    private final ChecklistRepository repository;
    private final UserRepository userRepository; 
    private final SubmissionRepository submissionRepository; 
    private final TimerService timerService; 

    public ChecklistService(ChecklistRepository repository, UserRepository userRepository,
                            SubmissionRepository submissionRepository, TimerService timerService) { 
        this.repository = repository;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.timerService = timerService;
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponse> getChecklistByUser(Long userId) {
        List<Checklist> checklists = repository.findByUserId(userId);
        if (checklists.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> checklistIds = checklists.stream().map(Checklist::getId).collect(Collectors.toList());
        
        // N+1 최적화된 명수 카운트 조회
        Map<Long, Long> participantCounts = submissionRepository.countSubmissionsByChecklistIds(checklistIds).stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).longValue(),
                        result -> ((Number) result[1]).longValue() 
                ));

        return checklists.stream()
                .map(checklist -> {
                    Long participantCount = participantCounts.getOrDefault(checklist.getId(), 0L);
                    
                    Long durationMinutes = timerService.calculateDurationMinutes(
                            checklist.getStartTime(), checklist.getEndTime()); 
                            
                    return ChecklistResponse.from(checklist, participantCount, durationMinutes);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Checklist createChecklist(ChecklistRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        Checklist checklist = Checklist.builder()
                .content(request.getContent())
                .description(request.getDescription())
                .user(user)
                .build();

        return repository.save(checklist);
    }

    @Transactional
    public void updateChecklist(Long id, ChecklistUpdateRequest request, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(id, userId);
        checklist.updateContent(request.getContent());
        checklist.updateDescription(request.getDescription());
    }

    @Transactional
    public void markComplete(Long id, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(id, userId);
        checklist.complete();
    }

    @Transactional
    public void deleteChecklist(Long id, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(id, userId);
        repository.delete(checklist);
    }

    @Transactional
    public void startChecklistSession(Long checklistId, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(checklistId, userId);
        checklist.startSession();
    }

    @Transactional
    public void endChecklistSession(Long checklistId, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(checklistId, userId);
        checklist.endSession();
    }
    
    private Checklist findChecklistByIdAndOwner(Long checklistId, Long userId) {
        return repository.findById(checklistId)
                .filter(checklist -> checklist.getUser().getId().equals(userId))
                .orElseThrow(() -> new NoSuchElementException(
                        "Checklist not found with id: " + checklistId + " for the current user"));
    }
}