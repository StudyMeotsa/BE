package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.dto.SubmissionCreateDto;
import com.example.growingstudy.session.dto.SubmissionResponseDto;
import com.example.growingstudy.session.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/checklists/{checklistId}/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    // 통합된 생성 메서드 (MultipartFile 포함)
    @PostMapping
    public ResponseEntity<Long> createSubmission(
            @PathVariable Long checklistId,
            @RequestPart("dto") SubmissionCreateDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(submissionService.createSubmission(checklistId, dto, image));
    }

    @GetMapping
    public ResponseEntity<List<SubmissionResponseDto>> getSubmissions(@PathVariable Long checklistId) {
        List<SubmissionResponseDto> response = submissionService.getSubmissionsByChecklist(checklistId);
        return ResponseEntity.ok(response);
    }
}