package com.checklist.controller;

import com.checklist.dto.ChecklistRequest;
import com.checklist.dto.ChecklistResponse;
import com.checklist.service.ChecklistService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> createChecklist(@RequestBody ChecklistRequest request) {
        service.createChecklist(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ChecklistResponse>> getChecklist(@RequestParam Long userId) {
        return ResponseEntity.ok(service.getChecklistByUser(userId));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> markComplete(@PathVariable Long id) {
        service.markComplete(id);
        return ResponseEntity.ok().build();
    }
}