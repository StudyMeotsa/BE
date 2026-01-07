package com.example.growingstudy.groupsub.service;

import com.example.growingstudy.groupsub.entity.TotalStudyTime;
import com.example.growingstudy.groupsub.entity.StudyTimeLog;
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

    // StudyTime 저장
    public void logStudyTime (Long accountId, Long groupId, Long sessionId, Integer time, LocalDateTime createdAt) {

        GroupMember member = groupMemberRepository.findByAccountIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 세션이 없습니다."));

        TotalStudyTime totalStudyTime = studyTimeRepository.findByMemberAndSession(member, session)
                .orElseGet(() -> studyTimeRepository.save(TotalStudyTime.create(createdAt ,member, session))); // 없으면 생성

        timeLogRepository.save(StudyTimeLog.create(time, createdAt, totalStudyTime));

        totalStudyTime.addMinutes(time, createdAt);
    }

    // StudyTimeLog 조회
    public List<TimeLogsResponse> getStudyTimeLogs(Long accountId, Long groupId, Long sessionId) {
        GroupMember member = groupMemberRepository.findByAccountIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        return timeLogRepository
                .findByTotalStudyTimeMemberIdAndTotalStudyTimeSessionId(member.getId(), sessionId)
                .stream()
                .map(v ->new TimeLogsResponse(
                        v.getId(),
                        v.getTime(),
                        v.getCreatedAt()
                ))
                .toList();
    }
}
