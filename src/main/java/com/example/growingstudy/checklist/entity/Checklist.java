package com.example.growingstudy.checklist.entity;

import com.example.growingstudy.group.entity.Group;
import com.example.growingstudy.submission.entity.Submission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;        // 할 일 제목

    @Column(columnDefinition = "TEXT")
    private String description;    // 상세 설명

    private boolean completed = false;  // 기본값 false

    @Column(nullable = false)
    private LocalDateTime startTime;    // 세션 시작

    @Column(nullable = false)
    private LocalDateTime endTime;      // 세션 종료

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;                // 그룹 연결 (참조만)

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();  // 제출 내역

    // 생성자
    public Checklist(String content, String description, LocalDateTime startTime, LocalDateTime endTime, Group group) {
        this.content = content;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.group = group;
        this.completed = false;
    }
}