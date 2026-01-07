package com.example.growingstudy.groupsub.controller;

import com.example.growingstudy.groupsub.dto.CreateNoticeRequest;
import com.example.growingstudy.groupsub.dto.CurrentNoticeResponse;
import com.example.growingstudy.groupsub.service.GroupNoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studyrooms/{groupId}/notice")
public class GroupNoticeController {

    private final GroupNoticeService groupNoticeService;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> createNotice(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            //Todo: 검증을 dto로 옮기는 거 고려
            @RequestBody @Valid CreateNoticeRequest request) {

        Long accountId = Long.parseLong(auth.getSubject());

        groupNoticeService.createNotice(accountId, groupId, request.title(), request.content());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true));
    }

    @GetMapping
    public ResponseEntity<CurrentNoticeResponse> getNotice(
            @PathVariable Long groupId) {

        return ResponseEntity
                .ok(groupNoticeService.getCurrentNotice(groupId));
    }
}
