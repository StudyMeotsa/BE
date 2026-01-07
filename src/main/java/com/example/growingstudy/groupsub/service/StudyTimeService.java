package com.example.growingstudy.groupsub.service;

import com.example.growingstudy.groupsub.entity.StudyTime;
import com.example.growingstudy.groupsub.entity.TimeLog;
import com.example.growingstudy.groupsub.repository.StudyTimeRepository;
import com.example.growingstudy.groupsub.repository.TimeLogRepository;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.studygroup.entity.GroupMember;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import com.example.growingstudy.groupsub.dto.TimeLogsResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyTimeService {

    private final GroupMemberRepository groupMemberRepository;
    private final SessionRepository sessionRepository;
    private final StudyTimeRepository studyTimeRepository;
    private final TimeLogRepository timeLogRepository;

    public void logStudyTime (Long accountId, Long groupId, Long sessionId, Integer time, LocalDateTime createdAt) {

        GroupMember member = groupMemberRepository.findByAccountIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 세션이 없습니다."));

        StudyTime totalStudyTime = studyTimeRepository.findByMemberAndSession(member, session)
                .orElseGet(() -> studyTimeRepository.save(StudyTime.create(createdAt ,member, session))); // 없으면 생성

        timeLogRepository.save(TimeLog.create(time, createdAt, totalStudyTime));

        totalStudyTime.addMinutes(time, createdAt);
    }

    public List<TimeLogsResponse> getStudyTimeLogs(Long accountId, Long groupId, Long sessionId) {
        GroupMember member = groupMemberRepository.findByAccountIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        return timeLogRepository
                .findByStudyTimeMemberIdAndStudyTimeSessionId(member.getId(), sessionId)
                .stream()
                .map(v ->new TimeLogsResponse(
                        v.getId(),
                        v.getTime(),
                        v.getCreatedAt()
                ))
                .toList();
    }
}
