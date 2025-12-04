package com.example.growingstudy.checklist.entity;

import com.example.growingstudy.group.entity.Group;
import com.example.growingstudy.submission.entity.Submission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean completed = false;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();

    public Checklist(String content, String description, LocalDateTime startTime, LocalDateTime endTime, Group group) {
        this.content = content;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.group = group;
        this.completed = false;
    }

    // ✅ 체크리스트 완료 처리
    public void complete() {
        this.completed = true;
    }

    // ✅ 세션 시작 검증
    public void startSession() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(this.startTime)) {
            throw new IllegalStateException("아직 세션 시작 시간이 되지 않았습니다.");
        }
        if (this.completed) {
            throw new IllegalStateException("이미 완료된 체크리스트는 세션을 시작할 수 없습니다.");
        }
    }

    // ✅ 세션 종료 검증
    public void endSession() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(this.endTime)) {
            throw new IllegalStateException("아직 세션 종료 시간이 되지 않았습니다.");
        }
        if (!this.completed) {
            throw new IllegalStateException("완료되지 않은 체크리스트는 세션을 종료할 수 없습니다.");
        }
    }
}