package com.checklist;

import com.checklist.dto.ChecklistRequest;
import com.checklist.dto.ChecklistResponse;
import com.checklist.entity.Checklist;
import com.checklist.repository.ChecklistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChecklistService {

    private final ChecklistRepository repository;

    public ChecklistService(ChecklistRepository repository) {
        this.repository = repository;
    }

    public void createChecklist(ChecklistRequest request) {
        Checklist checklist = new Checklist();
        checklist.setContent(request.getContent());
        checklist.setCompleted(false);
        checklist.setUserId(request.getUserId());
        repository.save(checklist);
    }

    public List<ChecklistResponse> getChecklistByUser(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(c -> new ChecklistResponse(c.getId(), c.getContent(), c.isCompleted()))
                .collect(Collectors.toList());
    }

    public void markComplete(Long id) {
        Checklist checklist = repository.findById(id).orElseThrow();
        checklist.setCompleted(true);
        repository.save(checklist);
    }
}