package com.example.growingstudy.session.service;

import com.example.growingstudy.session.entity.Submission;
import com.example.growingstudy.session.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository repository;

    public SubmissionService(SubmissionRepository repository) {
        this.repository = repository;
    }

    public Submission createSubmission(Submission submission) {
        if (repository.existsByChecklistId(submission.getChecklist().getId())) {
            throw new IllegalStateException("이미 제출된 체크리스트입니다.");
        }
        return repository.save(submission);
    }

    public List<Submission> getSubmissions(Long checklistId) {
        return repository.findByChecklistId(checklistId);
    }
}