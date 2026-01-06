package com.example.growingstudy.studygroup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.growingstudy.auth.entity.Account;

import java.time.LocalDateTime;
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
    @Column(nullable = false, length = 30)
    private String name;

    @Column(name = "start_day", nullable = false)
    private LocalDateTime startDay;

    @Column(name = "week_session", nullable = false)
    private Integer weekSession;

    @Column(name = "total_week", nullable = false)
    private Integer totalWeek;

    @Column(name = "max_member", nullable = false)
    private Integer maxMember;

    @Column(name = "session_hour", nullable = false)
    private Integer sessionHour;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 8)
    private String code;

    public StudyGroup(String name, LocalDateTime startDay, Integer weekSession, Integer totalWeek, Integer sessionHour, Integer maxMember, String description, Account account) {
        this.name = name;
        this.weekSession = weekSession;
        this.startDay = startDay;
        this.totalWeek = totalWeek;
        this.sessionHour = sessionHour;
        this.maxMember = maxMember;
        this.description = description;
        this.code = UUID.randomUUID().toString().substring(0, 8);
    }
}
