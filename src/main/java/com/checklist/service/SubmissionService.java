package com.checklist.service;

import com.checklist.dto.SubmissionRequest;
import com.checklist.dto.SubmissionResponse;
import com.checklist.entity.Checklist;
import com.checklist.entity.Submission;
import com.checklist.entity.User;
import com.checklist.exception.DuplicateSubmissionException;
import com.checklist.repository.ChecklistRepository;
import com.checklist.repository.SubmissionRepository;
import com.checklist.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ChecklistRepository checklistRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository, 
                             ChecklistRepository checklistRepository, 
                             UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.checklistRepository = checklistRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long createSubmission(Long checklistId, SubmissionRequest request, Long userId) {
        
        User submitter = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new NoSuchElementException("Checklist not found with id: " + checklistId));
        
        // 중복 제출 방지 로직
        submissionRepository.findByChecklistIdAndSubmitterId(checklistId, userId)
                .ifPresent(s -> { throw new DuplicateSubmissionException("User " + userId + " has already submitted for checklist " + checklistId); });

        Submission submission = Submission.builder()
                .checklist(checklist)
                .submitter(submitter)
                .submissionData(request.getSubmissionData())
                .build();

        return submissionRepository.save(submission).getId();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByChecklist(Long checklistId, Long requestingUserId) {
        return submissionRepository.findByChecklistId(checklistId).stream()
                .map(SubmissionResponse::from)
                .collect(Collectors.toList());
    }
}