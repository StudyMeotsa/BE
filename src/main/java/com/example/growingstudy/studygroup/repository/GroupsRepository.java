package com.example.growingstudy.studygroup.repository;

import com.example.growingstudy.studygroup.dto.GroupsListView;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupsRepository extends JpaRepository<StudyGroup, Long> {
    Optional<StudyGroup> findByCode(String Code);
    boolean existsByCode(String Code);

    @Query(value = """
        SELECT
            g.id                               AS groupId,
            g.name                             AS name,
            g.start_day                        AS startDay,
            DATE_ADD(g.start_day, INTERVAL g.total_week WEEK) AS endDay,
            g.week_session                     AS weekSession,
            (g.total_week * g.week_session)    AS totalSessions,
            g.session_hour                     AS sessionHour,
            (SELECT COUNT(*) FROM group_member gm2 WHERE gm2.group_id = g.id) AS currentMember,
            g.max_member                       AS maxMember,

            (SELECT s.id
               FROM session s
              WHERE s.group_id = g.id)                         AS sessionId,

            ct.name                            AS coffee,
            gc.level                           AS coffeeLevel
        FROM group_member gm
        JOIN groups g
          ON g.id = gm.group_id

        LEFT JOIN group_coffee gc
          ON gc.group_id = g.id
        LEFT JOIN coffee_type ct
          ON ct.id = gc.type_id

        WHERE gm.member_id = :memberId
          AND g.start_day <= :now
          AND DATE_ADD(g.start_day, INTERVAL g.total_week WEEK) >= :now

        ORDER BY g.start_day DESC
        """, nativeQuery = true)
    List<GroupsListView> findGroupsByMember(
            @Param("memberId") Long memberId,
            @Param("now") LocalDateTime now
    );
}
