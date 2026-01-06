package com.example.growingstudy.studygroup.controller;

import com.example.growingstudy.studygroup.dto.CreateGroupRequest;
import com.example.growingstudy.studygroup.dto.CreateGroupResponse;
import com.example.growingstudy.studygroup.dto.GroupListResponse;
import com.example.growingstudy.studygroup.dto.GroupListView;
import com.example.growingstudy.studygroup.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyrooms")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {this.groupService = groupService;}

    @PostMapping
    public ResponseEntity<CreateGroupResponse> createStudyRoom(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateGroupRequest request
    ) {
        Long accountId = Long.valueOf(jwt.getSubject());

        String code =
                groupService.createGroup(
                        accountId,
                        request.name(),
                        request.startDay(),
                        request.weekSession(),
                        request.totalWeek(),
                        request.sessionHour(),
                        request.maxMember(),
                        request.description()
                );

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
    public List<GroupListResponse> getStudyRoomList(@AuthenticationPrincipal Jwt auth) {
        Long accountId = Long.parseLong(auth.getSubject());
        return groupService.getGroupsList(accountId);
    }

}
