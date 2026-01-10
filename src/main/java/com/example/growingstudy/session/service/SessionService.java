package com.example.growingstudy.session.service;

import com.example.growingstudy.session.dto.SessionInfoResponse;
import com.example.growingstudy.session.dto.SessionInfoRequest;
import com.example.growingstudy.session.dto.SessionProgressResponse;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.session.repository.SessionRepository;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import com.example.growingstudy.studygroup.repository.GroupMemberRepository;
import com.example.growingstudy.studygroup.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public SessionInfoResponse getSessionInfo(Long accountId, Long groupId, Long sessionId) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

        Session session = sessionRepository.findByIdAndGroup_Id(sessionId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("세션이 존재하지 않습니다."));

        return SessionInfoResponse.from(session);
    }

    public void createSession(Long accountId, Long groupId, SessionInfoRequest request) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));

        Session session = Session.of(
                request.sessionOrder(),
                request.title(),
                request.startTime(),
                request.endTime(),
                group);

        sessionRepository.save(session);
    }

    public SessionProgressResponse getSessionProgress(Long accountId, Long groupId) {

        if (!groupMemberRepository.existsByAccount_IdAndGroup_Id(accountId, groupId)) {
            throw new IllegalArgumentException("그룹에 가입되어 있지 않습니다.");
        }

        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));


        return new SessionProgressResponse(
                sessionRepository.countCompletedSessions(groupId),
                group.getWeekSession() * group.getWeekSession());


    }

//    업데이트긴 한데 안쓸거같아서 주석처리
//
//    public void updateSession(Long accountId, Long groupId, Long sessionId, SessionInfoRequest request) {
//
//        // 멤버 검증 로직 추가(accountId, groupId)
//
//        StudyGroup group = groupRepository.findById(groupId)
//                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));
//
//        Session session = sessionRepository.findByIdAndGroup_Id(sessionId, groupId)
//                .orElseThrow(() -> new IllegalArgumentException("세션이 존재하지 않습니다."));
//
//        //dirty checking으로 자동 업데이트
//        session.update(request, group);
//    }
}
