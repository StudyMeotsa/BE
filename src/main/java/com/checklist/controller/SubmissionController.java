package com.checklist.controller;

import com.checklist.dto.SubmissionRequest;
import com.checklist.dto.SubmissionResponse;
import com.checklist.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/checklists/{checklistId}/submissions")
public class SubmissionController {
    
    private final SubmissionService submissionService;
    
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }
    
    @PostMapping
    public ResponseEntity<Void> createSubmission(
            @PathVariable Long checklistId, 
            @Valid @RequestBody SubmissionRequest request, 
            @AuthenticationPrincipal Long userId) {
        
        Long newSubmissionId = submissionService.createSubmission(checklistId, request, userId);
        
        return ResponseEntity.created(
                URI.create("/api/checklists/" + checklistId + "/submissions/" + newSubmissionId)
        ).build();
    }
    
    @GetMapping
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByChecklist(
            @PathVariable Long checklistId, 
            @AuthenticationPrincipal Long requestingUserId) {
        
        List<SubmissionResponse> submissions = submissionService.getSubmissionsByChecklist(checklistId, requestingUserId);
        
        return ResponseEntity.ok(submissions);
    }
}