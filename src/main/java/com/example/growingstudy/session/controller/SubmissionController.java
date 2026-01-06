package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.dto.SubmissionCreateDto;
import com.example.growingstudy.session.dto.SubmissionResponseDto;
import com.example.growingstudy.session.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists/{checklistId}/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<Long> createSubmission(
            @PathVariable Long checklistId,
            @RequestBody SubmissionCreateDto dto) {
        Long id = submissionService.createSubmission(checklistId, dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<List<SubmissionResponseDto>> getSubmissions(@PathVariable Long checklistId) {
    List<SubmissionResponseDto> response = submissionService.getSubmissionsByChecklist(checklistId);
    return ResponseEntity.ok(response);
    }
}
