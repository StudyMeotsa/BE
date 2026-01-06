package com.example.growingstudy.studytime.entity;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.studygroup.entity.GroupMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_time", nullable = false)
    private Integer totalTime;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private GroupMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public StudyTime(Integer totalTime, GroupMember member, Session session) {
        this.totalTime = totalTime;
        this.member = member;
        this.session = session;
    }
}
