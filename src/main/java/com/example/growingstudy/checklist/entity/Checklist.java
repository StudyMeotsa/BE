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
    private Long id; // 체크리스트 고유 ID

    @Column(nullable = false)
    private String content; // 체크리스트 항목 내용

    @Column(columnDefinition = "TEXT")
    private String description; // 상세 설명

    @Column(nullable = false)
    private boolean completed = false; // 완료 여부

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // 세션 시작 시간

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // 세션 종료 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // 연결된 그룹 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session; // 연결된 세션 ID

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    // 생성자
    public Checklist(String content, String description, LocalDateTime startTime, LocalDateTime endTime, 
                     Group group, Session session) {
        this.content = content;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.group = group;
        this.session = session;
        this.completed = false;
    }
}