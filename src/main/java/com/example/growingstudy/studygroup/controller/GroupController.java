package com.example.growingstudy.studygroup.controller;

import com.example.growingstudy.studygroup.dto.GroupsListView;
import com.example.growingstudy.studygroup.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<GroupsListView> getStudyRooms(@RequestParam Long accountId) {
        return groupService.getGroupsList(accountId);
    }


//    Todo: 토큰 db 연결시 사용
//    public List<GroupsListView> getStudyRooms(@AuthenticationPrincipal Jwt auth) {
//        Long memberId = Long.parseLong(auth.getSubject());
//        return groupService.getGroupsList(memberId);
//    }

}
