package com.example.growingstudy.studygroup.controller;

import com.example.growingstudy.studygroup.dto.*;
import com.example.growingstudy.studygroup.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/studyrooms")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {this.groupService = groupService;}

    /**
     * 그룹 생성
     * @param auth JWT 엑세스 토큰
     * @param request 그룹 생성 요청 정보
     * @return code
     */
    @PostMapping
    public ResponseEntity<CreateGroupResponse> createStudyRoom(
            @AuthenticationPrincipal Jwt auth,
            //Todo: 검증을 dto로 옮기는 거 고려
            @RequestBody @Valid CreateGroupRequest request
    ) {
        Long accountId = Long.valueOf(auth.getSubject());

        String code = groupService.createGroup(accountId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateGroupResponse(code));
    }

    /**
     * 그룹 리스트 조회
     *
     * @param auth JWT 엑세스 토큰
     * @return 그룹 리스트
     */
    @GetMapping
    public List<GroupListInfoResponse> getStudyRoomList(
            @AuthenticationPrincipal Jwt auth) {

        Long accountId = Long.parseLong(auth.getSubject());

        return groupService.getGroupList(accountId);
    }

    /**
     * 그룹 나가기
     * @param auth JWT 엑세스 토큰
     * @param groupId 그룹 ID
     * @return 성공 or 400
     */
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Map<String, Boolean>> deleteStudyRoom(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId) {

        Long accountId = Long.parseLong(auth.getSubject());
        groupService.deleteGroup(accountId, groupId);

        return ResponseEntity
                .ok(Map.of("success", true));
    }

    /**
     * 그룹 정보 조회
     * @param groupId 그룹 아이디
     * @return 그룹 정보
     */
    @GetMapping("/{groupId}")
    public GroupInfoResponse getStudyRoom(
            @AuthenticationPrincipal Jwt auth,
            @PathVariable Long groupId) {

        Long accountId = Long.parseLong(auth.getSubject());

        return groupService.getGroupInfo(accountId, groupId);
    }

    /**
     * 초대코드로 그룹 조인
     * @param auth jwt 엑세스토큰
     * @param request 초대코드
     * @return 성공 or 400
     */
    @PostMapping("/join")
    public ResponseEntity<Map<String, Boolean>> joinStudyRoom(
            @AuthenticationPrincipal Jwt auth,
            //Todo: 검증을 dto로 옮기는 거 고려
            @RequestBody @Valid JoinGroupRequest request) {

        Long accountId = Long.parseLong(auth.getSubject());

        groupService.joinGroup(accountId, request.code());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true));
    }
}
