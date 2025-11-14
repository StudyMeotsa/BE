package com.checklist.controller;

import com.checklist.dto.ChecklistRequest;
import com.checklist.dto.ChecklistResponse;
import com.checklist.dto.ChecklistUpdateRequest;
import com.checklist.service.ChecklistService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {
    
    private final ChecklistService service;

    public ChecklistController(ChecklistService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> createChecklist(@Valid @RequestBody ChecklistRequest request, @AuthenticationPrincipal Long userId) {
        com.checklist.entity.Checklist checklist = service.createChecklist(request, userId);
        return ResponseEntity.created(URI.create("/api/checklists/" + checklist.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<ChecklistResponse>> getMyChecklists(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(service.getChecklistByUser(userId));
    }

    @PatchMapping("/{id}/start-session")
    public ResponseEntity<Void> startSession(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        service.startChecklistSession(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/end-session")
    public ResponseEntity<Void> endSession(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        service.endChecklistSession(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> markComplete(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        service.markComplete(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        service.deleteChecklist(id, userId); 
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateChecklist(
            @PathVariable Long id, 
            @Valid @RequestBody ChecklistUpdateRequest request, 
            @AuthenticationPrincipal Long userId) {
        service.updateChecklist(id, request, userId); 
        return ResponseEntity.noContent().build();
    }
}