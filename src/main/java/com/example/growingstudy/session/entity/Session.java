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

    @Column(name = "session_order", nullable = false)
    private Integer sessionOrder;

    @Column(name = "title", length = 150)
    private String title; // title VARCHAR(150)

    @Column(name = "start_time")
    private LocalDateTime startTime; // start_time DATETIME

    @Column(name = "end_time")
    private LocalDateTime endTime; // end_time DATETIME

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group; // group_id BIGINT (FK)

    private Session(Integer sessionOrder, String title, LocalDateTime startTime, LocalDateTime endTime, StudyGroup group) {
        this.sessionOrder = sessionOrder;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.group = group;
    }

    public static Session createFirst(LocalDateTime startTime, LocalDateTime endTime, StudyGroup group) {
        return new Session(1, null , startTime, endTime, group);
    }
}