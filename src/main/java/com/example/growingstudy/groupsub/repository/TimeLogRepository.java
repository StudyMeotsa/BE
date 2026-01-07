package com.example.growingstudy.groupsub.repository;

import com.example.growingstudy.groupsub.entity.StudyTimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeLogRepository extends JpaRepository<StudyTimeLog, Long> {
    List<StudyTimeLog> findByTotalStudyTimeMemberIdAndTotalStudyTimeSessionId(Long memberId, Long sessionId);
}
