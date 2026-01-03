package com.example.growingstudy.studyGroup.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.growingstudy.auth.entity.Account;
import lombok.Setter;

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
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startDay;

    @Column(nullable = false)
    private Integer weekSession;

    @Column(nullable = false)
    private Integer totalWeek;

    @Column(nullable = false)
    private Integer maxMember;

    @Column(nullable = false)
    private Integer sessionHour;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
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
