package com.checklist.controller;

import com.checklist.entity.Checklist;
import com.checklist.service.ChecklistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {

    private final ChecklistService service;

    public ChecklistController(ChecklistService service) {
        this.service = service;
    }

    @PostMapping
    public Checklist create(@RequestBody Checklist checklist) {
        return service.createChecklist(checklist);
    }

    @GetMapping("/group/{groupId}")
    public List<Checklist> getChecklists(@PathVariable Long groupId) {
        return service.getChecklists(groupId);
    }

    @PatchMapping("/{id}/complete")
    public void markComplete(@PathVariable Long id) {
        service.markComplete(id);
    }

    @PatchMapping("/{id}/start-session")
    public void startSession(@PathVariable Long id) {
        service.startSession(id);
    }

    @PatchMapping("/{id}/end-session")
    public void endSession(@PathVariable Long id) {
        service.endSession(id);
    }

    @GetMapping("/{id}/duration")
    public Long getDuration(@PathVariable Long id) {
        return service.calculateDurationMinutes(id);
    }

    @GetMapping("/{id}/progress")
    public int getProgressRate(@PathVariable Long id) {
        return service.calculateProgressRate(id);
    }
}