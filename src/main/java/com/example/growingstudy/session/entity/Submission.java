package com.example.growingstudy.session.entity;

import com.example.growingstudy.studyGroup.entity.GroupMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "submission")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // content TEXT (제출 내용)

    @Column(name = "image_path", length = 255)
    private String imagePath; // image_path VARCHAR(255)

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false; // is_verified BOOLEAN (Default False)

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // submitted_at DATETIME

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist; // checklist_id BIGINT (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id", nullable = false)
    private GroupMember submitter; // submitter_id BIGINT (FK: group_member)

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now(); // 제출 시간 자동 생성
    }

    public Submission(String content, String imagePath, Checklist checklist, GroupMember submitter) {
        this.content = content;
        this.imagePath = imagePath;
        this.checklist = checklist;
        this.submitter = submitter;
        this.isVerified = false;    // 제출 시 초기값은 false
    }
}