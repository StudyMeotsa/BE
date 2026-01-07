package com.example.growingstudy.groupsub.service;

import com.example.growingstudy.groupsub.dto.CurrentNoticeResponse;
import com.example.growingstudy.groupsub.entity.GroupNotice;
import com.example.growingstudy.groupsub.repository.GroupNoticeRepository;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.studygroup.entity.GroupMember;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupNoticeService {

    private final GroupNoticeRepository groupNoticeRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SessionRepository sessionRepository;

    // 공지글 생성
    // Todo: 방장 여부 확인
    public void createNotice (Long accountId, Long groupId, String title, String content) {

        GroupMember member = groupMemberRepository.findByAccountIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        GroupNotice groupNotice = GroupNotice.create(title, content, member);
        groupNoticeRepository.save(groupNotice);
    }

    // 최근 공지글 조회
    public CurrentNoticeResponse getCurrentNotice (Long groupId) {

        GroupNotice groupNotice = groupNoticeRepository.findTopByMember_Group_IdOrderByCreatedAtDesc(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹의 공지글이 없습니다."));

        return CurrentNoticeResponse.from(groupNotice);

    }
}
