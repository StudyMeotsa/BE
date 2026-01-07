package com.example.growingstudy.groupsub.controller;

import com.example.growingstudy.groupsub.dto.LogStudyTimeRequest;
import com.example.growingstudy.groupsub.dto.TimeLogsResponse;
import com.example.growingstudy.groupsub.service.StudyTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studyrooms/{groupId}/{sessionId}")
public class StudyTimeController {

    private final StudyTimeService studyTimeService;

    @PostMapping("/study")
    public ResponseEntity<Map<String, Boolean>> logStudyTime(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @PathVariable Long sessionId,
            //Todo: 검증을 dto로 옮기는 거 고려
            @RequestBody @Valid LogStudyTimeRequest request) {

        Long accountId = Long.parseLong(auth.getSubject());

        studyTimeService.logStudyTime(accountId, groupId, sessionId, request.time(), request.createdAt());

        return ResponseEntity
                .ok(Map.of("success", true));
    }

    @GetMapping("/studylogs")
    public ResponseEntity<List<TimeLogsResponse>> getStudyTimeLogs(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @PathVariable Long sessionId) {

        Long accountId = Long.parseLong(auth.getSubject());

        return ResponseEntity
                .ok(studyTimeService.getStudyTimeLogs(accountId, groupId, sessionId));
    }
}
