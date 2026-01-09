package com.example.growingstudy.studygroup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "study_group")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 생성 시 입력 사항
    @Column(nullable = false, length = 30, unique = true)
    private String name;

    @Column(name = "start_day", nullable = false)
    private LocalDate startDay;

    @Column(name = "week_session", nullable = false)
    private Integer weekSession;

    @Column(name = "total_week", nullable = false)
    private Integer totalWeek;

    @Column(name = "max_member", nullable = false)
    private Integer maxMember;

    @Column(name = "study_time_aim", nullable = false)
    private Integer studyTimeAim;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 8)
    private String code;

    private StudyGroup(String name, LocalDate startDay, Integer weekSession, Integer totalWeek, Integer maxMember, Integer studyTimeAim, String description) {
        this.name = name;
        this.weekSession = weekSession;
        this.startDay = startDay;
        this.totalWeek = totalWeek;
        this.maxMember = maxMember;
        this.studyTimeAim = studyTimeAim;
        this.description = description;
        this.code = UUID.randomUUID().toString().substring(0, 8);
    }

    public static StudyGroup create(
            String name,
            LocalDate startDay,
            Integer weekSession,
            Integer totalWeek,
            Integer maxMember,
            Integer sessionHour,
            String description
    ) {
        //검증 로직 추가 필요
        return new StudyGroup(
                name,
                startDay,
                weekSession,
                totalWeek,
                maxMember,
                sessionHour,
                description
        );
    }
}
