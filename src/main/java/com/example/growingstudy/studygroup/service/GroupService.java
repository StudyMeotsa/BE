package com.example.growingstudy.studygroup.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.coffee.entity.CoffeeType;
import com.example.growingstudy.coffee.entity.GroupCoffee;
import com.example.growingstudy.coffee.repository.CoffeeTypeRepository;
import com.example.growingstudy.coffee.repository.GroupCoffeeRepository;
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
import java.util.NoSuchElementException;
import java.util.random.RandomGenerator;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final AccountRepository accountRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final CoffeeTypeRepository coffeeTypeRepository;
    private final GroupCoffeeRepository groupCoffeeRepository;

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

        assignRandomCoffee(group);

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

    // private 메소드
    private void assignRandomCoffee(StudyGroup group) {
        // 커피 타입 가져오기
        List<Long> level1CoffeeTypeIds = coffeeTypeRepository.findAllIdsLevel1CoffeeType();

        // 커피 타입 테이블에 아무것도 없을 때 예외 처리
        if (level1CoffeeTypeIds.isEmpty()) throw new NoSuchElementException("배정할 수 있는 커피 타입이 없습니다.");

        // 랜덤 커피 지정
        int idx = RandomGenerator.getDefault().nextInt(level1CoffeeTypeIds.size());
        Long coffeeTypeId = level1CoffeeTypeIds.get(idx);

        // 그룹 커피 정보 계산
        int totalSessions = group.getTotalWeek() * group.getWeekSession();
        int requiredBeansAll = totalSessions * 20;
        int requiredBeansPerLevel = requiredBeansAll / 5; // 레벨 개수 5로 고정

        // 그룹 커피 등록
        CoffeeType ctRef = coffeeTypeRepository.getReferenceById(coffeeTypeId); // 로딩 없이 참조만
        GroupCoffee gc = new GroupCoffee(group, ctRef, requiredBeansAll, requiredBeansPerLevel, 0, 1);
        groupCoffeeRepository.save(gc);
    }
}
