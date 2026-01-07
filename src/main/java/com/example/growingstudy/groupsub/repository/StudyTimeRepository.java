package com.example.growingstudy.groupsub.repository;

import com.example.growingstudy.groupsub.entity.TotalStudyTime;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.studygroup.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyTimeRepository extends JpaRepository<TotalStudyTime, Long> {
    Optional<TotalStudyTime> findByMemberAndSession(GroupMember member, Session session);
}
