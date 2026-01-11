package com.example.growingstudy.session.controller;

import com.example.growingstudy.session.dto.SessionInfoRequest;
import com.example.growingstudy.session.dto.SessionInfoResponse;
import com.example.growingstudy.session.dto.SessionProgressResponse;
import com.example.growingstudy.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
//groupId는 권한 확인할 때 필요할 거 같아서 일단 넣어둠
@RequestMapping("/api/studyrooms/{groupId}/sessions")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{sessionId}")
    public SessionInfoResponse getSessionInfo(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @PathVariable Long sessionId) {

        Long accountId = Long.parseLong(auth.getSubject());

        return sessionService.getSessionInfo(accountId, groupId, sessionId);
    }

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> createSession(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId,
            @RequestBody SessionInfoRequest request) {

        Long accountId = Long.parseLong(auth.getSubject());

        sessionService.createSession(accountId, groupId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true));
    }

    @GetMapping("/progress")
    public ResponseEntity<SessionProgressResponse> getSessionProgress(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId) {

        Long accountId = Long.parseLong(auth.getSubject());

        return ResponseEntity.ok(sessionService.getSessionProgress(accountId, groupId));
    }

//    업데이트 만들긴 했는데 안쓸거 같아서 주석처리
//
//    @PutMapping("/{sessionId}")
//    public ResponseEntity<Map<String, Boolean>>  updateSession(
//            @AuthenticationPrincipal Jwt auth,
//            @PathVariable Long groupId,
//            @PathVariable Long sessionId,
//            @RequestBody SessionInfoRequest request) {
//
//        Long accountId = Long.parseLong(auth.getSubject());
//
//        sessionService.updateSession(accountId, groupId, sessionId, request);
//
//        return ResponseEntity
//                .ok(Map.of("success", true));
//
//    }


}
