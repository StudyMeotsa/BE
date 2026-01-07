package com.example.growingstudy.groupsub.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_time_log")
public class StudyTimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer time;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_time_id", nullable = false)
    private TotalStudyTime totalStudyTime;

    private StudyTimeLog(Integer time, LocalDateTime createdAt, TotalStudyTime totalStudyTime) {
        this.time = time;
        this.createdAt = createdAt;
        this.totalStudyTime = totalStudyTime;
    }

    public static StudyTimeLog create(Integer time, LocalDateTime createdAt, TotalStudyTime totalStudyTime) {
        return new StudyTimeLog(time, createdAt, totalStudyTime);
    }
}
