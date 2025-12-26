package com.example.growingstudy.submission.entity;

import com.example.growingstudy.checklist.entity.Checklist;
import com.example.growingstudy.auth.entity.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "submission")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 제출 고유 ID

    @Column(columnDefinition = "TEXT")
    private String content; // 제출 내용 (텍스트)

    @Column(name = "image_path")
    private String imagePath; // S3 상대 경로

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // 제출 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist; // 연결된 체크리스트 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id", nullable = false)
    private Account submitter; // group_member fk

    @PrePersist
    public void onSubmit() { this.submittedAt = LocalDateTime.now(); }

    public Submission(String content, String imagePath, Checklist checklist, Account submitter) {
        this.content = content;
        this.imagePath = imagePath;
        this.checklist = checklist;
        this.submitter = submitter;
    }

    // 로직용 Enum (Checklist 필드에서는 제거됨)
    public enum SubmissionType { PHOTO, CHAT, TIMER }
}