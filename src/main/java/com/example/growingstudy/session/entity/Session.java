package com.example.growingstudy.session.entity;

import com.example.growingstudy.studygroup.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(name = "title", nullable = false, length = 150)
    private String title; // title VARCHAR(150)

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // start_time DATETIME

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // end_time DATETIME

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group; // group_id BIGINT (FK)

    @Builder
    public Session(String title, StudyGroup group, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
