package com.example.growingstudy.studygroup.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.studygroup.dto.CreateGroupRequest;
import com.example.growingstudy.studygroup.dto.GroupInfoResponse;
import com.example.growingstudy.studygroup.dto.GroupListInfoResponse;
import com.example.growingstudy.studygroup.entity.GroupMember;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import com.example.growingstudy.studygroup.repository.GroupRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final AccountRepository accountRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    //그룹 생성
    public String createGroup(Long accountId, CreateGroupRequest request){

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        if (groupRepository.existsByName(request.name())) {
            throw new IllegalStateException("이미 존재하는 그룹 이름입니다.");
        }

        StudyGroup group= StudyGroup.create(
                request.name(),
                request.startDay(),
                request.weekSession(),
                request.totalWeek(),
                request.studyTimeAim(),
                request.maxMember(),
                request.description());

        groupRepository.save(group);

        groupMemberRepository.save(GroupMember.of("ADMIN", group, account));

        // Todo: 그룹 커피 생성 로직 필요

        // 세션 자동 생성 -> 그룹 생성 후 세션 추가로 대체
//        LocalDateTime endDay = group.getStartDay().plusWeeks(group.getTotalWeek());
//
//        sessionRepository.save(Session.createFirst(startDay, endDay, group));

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
                        LocalDate.now()
                ).stream()
                .map(v -> new GroupListInfoResponse(
                        v.getGroupId(),
                        v.getSessionId(),
                        v.getName(),
                        v.getStartDay(),
                        v.getEndDay(),
                        v.getWeekSession(),
                        v.getTotalSessions(),
                        v.getStudyTimeAim(),
                        v.getCurrentMember(),
                        v.getMaxMember(),
                        v.getSessionOrder(),
                        v.getCoffee(),
                        v.getCoffeeLevel()
                ))
                .toList();
    }

    // 그룹 정보
    public GroupInfoResponse getGroupInfo(Long accountId, Long groupId) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

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
