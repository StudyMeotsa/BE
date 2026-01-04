package com.example.growingstudy.session.entity;

import com.example.growingstudy.studygroup.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // start_time DATETIME

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // end_time DATETIME

    public Session(String title, StudyGroup group, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
