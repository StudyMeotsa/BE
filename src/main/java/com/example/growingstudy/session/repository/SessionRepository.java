package com.example.growingstudy.session.repository;

import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByIdAndGroup_Id(Long sessionId, Long groupId);

    @Query(value = """
        SELECT COUNT(*) 
        FROM session s
        WHERE s.group_id = :groupId
          AND NOT EXISTS (
            SELECT 1
            FROM checklist c
            WHERE c.session_id = s.id
              AND c.completed = 0
          )
        """, nativeQuery = true)
    Long countCompletedSessions(@Param("groupId") Long groupId);


}
