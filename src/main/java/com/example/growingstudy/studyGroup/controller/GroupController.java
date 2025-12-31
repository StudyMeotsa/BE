package com.example.growingstudy.studyGroup.controller;

import com.example.growingstudy.studyGroup.dto.GroupsListView;
import com.example.growingstudy.studyGroup.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    //테스트용
    public List<GroupsListView> getStudyRooms(@RequestParam Long memberId) {
        return groupService.getGroupsList(memberId);
    }


//    Todo: 토큰 db 연결시 사용
//    public List<GroupsListView> getStudyRooms(@AuthenticationPrincipal Jwt auth) {
//        Long memberId = Long.parseLong(auth.getSubject());
//        return groupService.getGroupsList(memberId);
//    }

}
