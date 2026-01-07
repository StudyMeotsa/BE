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

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public TimeLog(Integer time, StudyTime studyTime) {
        this.time = time;
        this.studyTime = studyTime;
    }
}
