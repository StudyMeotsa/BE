package com.example.growingstudy.session.repository;

import com.example.growingstudy.session.dto.ChecklistsPerSessionView;
import com.example.growingstudy.session.entity.Checklist;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    List<ChecklistsPerSessionView> findProjectedBySessionId(Long sessionId);

    @Query(value = """
        SELECT NOT EXISTS (
          SELECT 1
          FROM checklist c
          WHERE c.session_id = :sessionId
            AND c.completed = 0
        )
        """, nativeQuery = true)
    int isAllCompleted(@Param("sessionId") Long sessionId);
}
