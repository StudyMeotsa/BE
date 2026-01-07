package com.example.growingstudy.auth.repository;

import com.example.growingstudy.auth.dto.MyCoffeeResponseDto;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MyCoffeesRepository extends JpaRepository<StudyGroup, Long> {

    /**
     * 해당 유저 id의 진행중 또는 완료된 스터디 목록과 커피 현황을 조회
     * @param accountId 유저 id
     * @return 스터디 목록과 커피 현황
     */
    // MySQL 네이티브 쿼리 사용
    @Query(value = """
    SELECT
        CASE
            WHEN NOW() < DATE_ADD(start_day, INTERVAL total_week WEEK) THEN 'inProgress'
            ELSE 'finished'
        END AS status,
        sg.id AS group_id, 
        sg.name AS group_name,
        ct.name AS type, 
        gc.level AS level,
        ct.image_path AS image_url
    FROM group_member gm
        JOIN study_group sg ON gm.group_id = sg.id
        JOIN group_coffee gc ON sg.id = gc.group_id
        JOIN coffee_type ct ON gc.type_id = ct.id
    WHERE gm.account_id = :accountId AND NOW() >= sg.start_day;
    """, nativeQuery = true)
    List<MyCoffeeResponseDto> findCoffeesByAccountId(Long accountId);
}