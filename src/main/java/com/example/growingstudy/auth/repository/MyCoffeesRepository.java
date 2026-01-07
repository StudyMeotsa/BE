package com.example.growingstudy.auth.repository;

import com.example.growingstudy.auth.dto.MyCoffeeResponseDto;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MyCoffeesRepository extends JpaRepository<StudyGroup, Long> {

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
    WHERE gm.account_id = :account_id AND start_day >= NOW();
    """, nativeQuery = true)
    List<MyCoffeeResponseDto> findMyCoffees(Long account_id);
}