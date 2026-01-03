package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.dto.ChecklistCreateDto;
import com.example.growingstudy.session.dto.ChecklistResponseDto;
import com.example.growingstudy.session.entity.Checklist;
import com.example.growingstudy.session.service.ChecklistService;
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
    public ChecklistResponseDto create(@RequestBody ChecklistCreateDto dto) {

        Checklist savedChecklist = service.createChecklist(dto);

        return ChecklistResponseDto.from(savedChecklist);
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