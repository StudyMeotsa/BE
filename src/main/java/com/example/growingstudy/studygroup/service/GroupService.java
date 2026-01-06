package com.example.growingstudy.studygroup.service;

import com.example.growingstudy.studygroup.dto.GroupsListView;
import com.example.growingstudy.studygroup.repository.GroupsRepository;
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

    public List<GroupsListView> getGroupsList(Long accountID) {
        return groupsRepository.findGroupsByMember(
                accountID,
                LocalDateTime.now()
        );
    }
}
