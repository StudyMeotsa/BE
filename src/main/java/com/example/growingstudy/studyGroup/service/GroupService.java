package com.example.growingstudy.studyGroup.service;

import com.example.growingstudy.studyGroup.dto.GroupsListView;
import com.example.growingstudy.studyGroup.repository.GroupsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class GroupService {
    private final GroupsRepository groupsRepository;

    public GroupService(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }

    public List<GroupsListView> getGroupsList(Long memberID) {
        return groupsRepository.findGroupsByMember(
                memberID,
                LocalDateTime.now()
        );
    }
}
