package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/studyrooms/{groupId}/sessions/{sessionId}/checklists/{checklistId}")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> createSubmission(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @PathVariable Long sessionId,
            @PathVariable Long checklistId,
            @RequestPart(required = false) String content,
            @RequestPart(required = false) MultipartFile file) {

        Long accountId = Long.parseLong(auth.getSubject());

        submissionService.createSubmission(accountId, groupId, sessionId, checklistId, content, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true));
    }

//    @GetMapping
//    public ResponseEntity<List<SubmissionResponseDto>> getSubmissions(@PathVariable Long checklistId) {
//    List<SubmissionResponseDto> response = submissionService.getSubmissionsByChecklist(checklistId);
//    return ResponseEntity.ok(response);
//    }

    @PatchMapping("/submissions/{submissionId}")
    public ResponseEntity<Map<String, Boolean>> verifySubmission(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @PathVariable Long sessionId,
            @PathVariable Long checklistId,
            @PathVariable Long submissionId) {

        Long accountId = Long.parseLong(auth.getSubject());

        submissionService.verifySubmission(accountId, groupId, sessionId, checklistId, submissionId);

        return ResponseEntity
                .ok(Map.of("success", true));
    }
}
