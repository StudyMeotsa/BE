package com.example.growingstudy.submission.entity;

import com.example.growingstudy.checklist.entity.Checklist;
import com.example.growingstudy.auth.entity.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submissionData;       // 제출 내용 (텍스트, 요약 등)
    private LocalDateTime submittedAt;   // 제출 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;         // 어떤 체크리스트에 대한 제출인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id", nullable = false)
    private Account submitter;           // 제출자 정보 (팀원 계정)

    @PrePersist
    public void onSubmit() {
        this.submittedAt = LocalDateTime.now();
    }

    public Submission(String submissionData, Checklist checklist, Account submitter) {
        this.submissionData = submissionData;
        this.checklist = checklist;
        this.submitter = submitter;
    }
}