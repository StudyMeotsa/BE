package com.example.growingstudy.submission.controller;

import com.example.growingstudy.submission.entity.Submission;
import com.example.growingstudy.submission.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists/{checklistId}/submissions")
public class SubmissionController {

    private final SubmissionService service;

    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @PostMapping
    public Submission createSubmission(@PathVariable Long checklistId, @RequestBody Submission submission) {
        submission.getChecklist().setId(checklistId);
        return service.createSubmission(submission);
    }

    @GetMapping
    public List<Submission> getSubmissions(@PathVariable Long checklistId) {
        return service.getSubmissions(checklistId);
    }
}