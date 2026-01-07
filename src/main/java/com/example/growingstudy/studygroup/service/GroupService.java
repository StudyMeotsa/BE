package com.example.growingstudy.studygroup.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.studygroup.dto.GroupInfoResponse;
import com.example.growingstudy.studygroup.dto.GroupListInfoResponse;
import com.example.growingstudy.studygroup.entity.GroupMember;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import com.example.growingstudy.studygroup.repository.GroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final AccountRepository accountRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SessionRepository sessionRepository;

    //그룹 생성
    public String createGroup(Long accountId, String name, LocalDateTime startDay, Integer weekSession, Integer totalWeek, Integer maxMember, Integer studyTimeAim, String description){

        if (groupRepository.existsByName(name)) {
            throw new IllegalStateException("이미 존재하는 그룹 이름입니다.");
        }
        StudyGroup group= StudyGroup.create(name, startDay, weekSession, totalWeek, maxMember, studyTimeAim, description);
        groupRepository.save(group);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));
        groupMemberRepository.save(GroupMember.of("ADMIN", group, account));

        //세션 생성
        int totalSession = group.getWeekSession() * group.getTotalWeek();
        LocalDateTime endDay = group.getStartDay().plusWeeks(group.getTotalWeek());

        sessionRepository.save(Session.createFirst(startDay, endDay, group));

        return group.getCode();
    }

    //그룹 나가기
    public void deleteGroup(Long accountId, Long groupId) {

        GroupMember groupMember = groupMemberRepository.findByAccountIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        groupMemberRepository.deleteById(groupMember.getId());
    }

    // 그룹 리스트
    public List<GroupListInfoResponse> getGroupList(Long accountId) {

        return groupRepository.findGroupsByAccountId(
                        accountId,
                        LocalDateTime.now()
                ).stream()
                .map(v -> new GroupListInfoResponse(
                        v.getGroupId(),
                        v.getName(),
                        v.getStartDay(),
                        v.getEndDay(),
                        v.getWeekSession(),
                        v.getTotalSessions(),
                        v.getStudyTimeAim(),
                        v.getCurrentMember(),
                        v.getMaxMember(),
                        v.getSessionId(),
                        v.getCoffee(),
                        v.getCoffeeLevel()
                ))
                .toList();
    }

    // 그룹 정보
    public GroupInfoResponse getGroupInfo(Long groupId) {

        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));

        return GroupInfoResponse.from(group);
    }

    // 입장코드로 그룹 등록
    public void joinGroup(Long accountId, String code) {

        StudyGroup group = groupRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 그룹이 없습니다."));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        GroupMember groupMember = GroupMember.of("MEMBER", group, account);
        groupMemberRepository.save(groupMember);
    }
}
