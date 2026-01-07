package com.example.growingstudy.studygroup.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.studygroup.dto.GroupListResponse;
import com.example.growingstudy.studygroup.entity.GroupMember;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import com.example.growingstudy.studygroup.repository.GroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class GroupService {
    private final AccountRepository accountRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupService(AccountRepository accountRepository, GroupRepository groupRepository, GroupMemberRepository groupMemberRepository) {
        this.accountRepository = accountRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    //그룹 생성
    public String createGroup(long accountId, String name, LocalDateTime startDay, Integer weekSession, Integer totalWeek, Integer maxMember, Integer sessionHour, String description){
        if (groupRepository.existsByName(name)) {
            throw new IllegalStateException("이미 존재하는 그룹 이름입니다.");
        }
        StudyGroup group= StudyGroup.create(name, startDay, weekSession, totalWeek, maxMember, sessionHour, description);
        groupRepository.save(group);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));
        GroupMember groupMember = new GroupMember("ADMIN", group, account);
        groupMemberRepository.save(groupMember);

        return group.getCode();
    }

    // 그룹 리스트
    public List<GroupListResponse> getGroupsList(Long accountId) {
        return groupRepository.findGroupsByMember(
                        accountId,
                        LocalDateTime.now()
                ).stream()
                .map(v -> new GroupListResponse(
                        v.getGroupId(),
                        v.getName(),
                        v.getStartDay(),
                        v.getEndDay(),
                        v.getWeekSession(),
                        v.getTotalSessions(),
                        v.getSessionHour(),
                        v.getCurrentMember(),
                        v.getMaxMember(),
                        v.getSessionId(),
                        v.getCoffee(),
                        v.getCoffeeLevel()
                ))
                .toList();
    }
}
