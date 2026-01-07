package com.example.growingstudy.groupsub.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer time;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_time_id", nullable = false)
    private StudyTime studyTime;

    private TimeLog(Integer time, LocalDateTime createdAt, StudyTime studyTime) {
        this.time = time;
        this.createdAt = createdAt;
        this.studyTime = studyTime;
    }

    public static TimeLog create(Integer time, LocalDateTime createdAt, StudyTime studyTime) {
        return new TimeLog(time, createdAt, studyTime);
    }
}
