package com.example.growingstudy.group.controller;

import com.example.growingstudy.group.entity.Group;
import com.example.growingstudy.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {this.groupService = groupService;}

    @PostMapping
    public ResponseEntity<String> createGroup(@RequestBody Group group) {
        try {
            Long groupId = groupService.join(group);
            return new ResponseEntity<>("그룹이 성공적으로 생성되었습니다. ID: " + groupId, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("그룹 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<Group> listGroups() {
        return groupService.findGroups();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable("groupId") Long groupId) {
        Optional<Group> group = groupService.findOne(groupId);

        return group.map(g -> new ResponseEntity<>(g, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
