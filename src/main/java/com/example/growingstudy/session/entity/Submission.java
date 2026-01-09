package com.example.growingstudy.session.entity;

import com.example.growingstudy.auth.entity.Account;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "submission")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // content VARCHAR(2000)

    @Column(name = "image_path", length = 255)
    private String imagePath; // image_path VARCHAR(255)

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false; // is_verified BOOLEAN

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // submitted_at DATETIME

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist; // checklist_id BIGINT (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id", nullable = false)
    private Account submitter; // submitter_id BIGINT (FK)

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
    }

    private Submission(String content, String imagePath, Checklist checklist, Account submitter) {
        this.content = content;
        this.imagePath = imagePath;
        this.checklist = checklist;
        this.submitter = submitter;
        this.isVerified = false; // 초기값
    }

    public static synchronized Submission create(String content, String imagePath, Checklist checklist, Account submitter) {

        if (content != null && !content.isBlank() && imagePath != null) {
            throw new IllegalArgumentException("내용 또는 이미지는 반드시 하나 이상 필요합니다.");
        }

        return new Submission(content, imagePath, checklist, submitter);
    }

    public void setIsVerifiedTrue() {
        this.isVerified = true;
    }
}