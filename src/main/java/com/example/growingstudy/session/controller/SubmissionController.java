package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.dto.SubmissionCreateDto;
import com.example.growingstudy.session.entity.Submission;
import com.example.growingstudy.session.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists/{checklistId}/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public Submission createSubmission(
            @PathVariable Long checklistId, 
            @RequestBody SubmissionCreateDto dto) {
        return submissionService.createSubmission(checklistId, dto);
    }

    @GetMapping
    public List<Submission> getSubmissions(@PathVariable Long checklistId) {
        return submissionService.getSubmissionsByChecklist(checklistId);
    }
}