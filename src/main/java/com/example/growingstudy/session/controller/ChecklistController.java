package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.dto.ChecklistCreateDto;
import com.example.growingstudy.session.dto.ChecklistResponseDto;
import com.example.growingstudy.session.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;

    // 체크리스트 생성
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ChecklistCreateDto dto) {
        Long id = checklistService.createChecklist(dto);
        return ResponseEntity.ok(id);
    }

    // 특정 세션에 속한 체크리스트 목록 조회
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ChecklistResponseDto>> getChecklistsBySession(@PathVariable Long sessionId) {
        List<ChecklistResponseDto> response = checklistService.getChecklistsBySession(sessionId);
        return ResponseEntity.ok(response);
    }
}
