package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.dto.ChecklistCreateRequest;
import com.example.growingstudy.session.dto.ChecklistOverviewResponse;
import com.example.growingstudy.session.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/studyrooms/{groupId}/sessions/{sessionId}/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    // 체크리스트 생성
    @PostMapping
    public ResponseEntity<Map<String, Boolean>> createChecklist(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @PathVariable Long sessionId,
            @RequestBody ChecklistCreateRequest request) {

        Long accountId = Long.parseLong(auth.getSubject());

        checklistService.createChecklist(accountId, groupId, sessionId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true));
    }

    // 특정 세션에 속한 체크리스트 목록 조회
    @GetMapping
    public ResponseEntity<ChecklistOverviewResponse> getChecklistsBySession(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @PathVariable Long sessionId) {

        Long accountId = Long.parseLong(auth.getSubject());

        return ResponseEntity
                .ok(checklistService.getChecklistsBySession(accountId, groupId, sessionId));
    }
}
