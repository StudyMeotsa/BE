package com.example.growingstudy.checklist.entity;

import com.example.growingstudy.group.entity.Group;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.submission.entity.Submission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "checklist")
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(nullable = false)
    private String content; // content VARCHAR

    @Column(columnDefinition = "TEXT")
    private String description; // description TEXT

    @Column(nullable = false)
    private boolean completed = false; // completed BOOLEAN

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // start_time DATETIME

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // end_time DATETIME

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // group_id BIGINT (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session; // session_id BIGINT (FK)

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    public Checklist(String content, String description, LocalDateTime startTime, LocalDateTime endTime, 
                     Group group, Session session) {
        this.content = content;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.group = group;
        this.session = session;
    }
}