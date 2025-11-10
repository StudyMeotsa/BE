package com.checklist.service;

import com.checklist.dto.ChecklistRequest;
import com.checklist.dto.ChecklistUpdateRequest;
import com.checklist.dto.ChecklistResponse;
import com.checklist.entity.Checklist;
import com.checklist.entity.User;
import com.checklist.repository.ChecklistRepository;
import com.checklist.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ChecklistService {

    private final ChecklistRepository repository;
    private final UserRepository userRepository;

    public ChecklistService(ChecklistRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Checklist createChecklist(ChecklistRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        Checklist checklist = Checklist.builder()
                .content(request.getContent())
                .user(user)
                .build();
        return repository.save(checklist); // 생성 시 completed는 builder에서 false로 자동 설정됩니다.
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponse> getChecklistByUser(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(ChecklistResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markComplete(Long checklistId, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(checklistId, userId);
        checklist.complete(); // 엔티티의 비즈니스 메소드 호출
    }

    @Transactional
    public void deleteChecklist(Long checklistId, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(checklistId, userId);
        repository.delete(checklist);
    }

    @Transactional
    public void updateChecklist(Long checklistId, ChecklistUpdateRequest request, Long userId) {
        Checklist checklist = findChecklistByIdAndOwner(checklistId, userId);
        checklist.updateContent(request.getContent());
    }

    /**
     * 체크리스트 ID와 사용자 ID로 리소스를 조회하고 소유권을 검증합니다.
     * 리소스가 없거나 소유권이 다르면 예외를 발생시킵니다.
     */
    private Checklist findChecklistByIdAndOwner(Long checklistId, Long userId) {
        return repository.findById(checklistId)
                .filter(checklist -> checklist.getUser().getId().equals(userId))
                .orElseThrow(() -> new NoSuchElementException(
                        "Checklist not found with id: " + checklistId + " for the current user"));
    }
}