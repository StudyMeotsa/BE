package com.example.growingstudy.groupsub.repository;

import com.example.growingstudy.groupsub.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
    List<TimeLog> findByStudyTimeMemberIdAndStudyTimeSessionId(Long memberId, Long sessionId);
}
