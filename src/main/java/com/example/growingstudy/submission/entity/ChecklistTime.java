package com.example.growingstudy.submission.entity;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.checklist.entity.Checklist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "checklist_time")
public class ChecklistTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 타이머 제출 고유 ID

    @Column(name = "time_aim")
    private Integer timeAim; // 목표 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Account member; // group_member fk

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission; // submission과 연결 (기존 time_id 역할 대체 가능)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session; // 연결된 세션 ID

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false; // 개별 멤버 목표 달성 여부

    public ChecklistTime(Integer timeAim, Account member, Submission submission, Session session, boolean isVerified) {
        this.timeAim = timeAim;
        this.member = member;
        this.submission = submission;
        this.session = session;
        this.isVerified = isVerified;
    }
}
