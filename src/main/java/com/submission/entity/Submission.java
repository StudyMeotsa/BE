package com.submission.entity;

import com.checklist.entity.Checklist;
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

    private String submissionData;
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @PrePersist
    public void onSubmit() {
        this.submittedAt = LocalDateTime.now();
    }

    public Submission(String submissionData, Checklist checklist) {
        this.submissionData = submissionData;
        this.checklist = checklist;
    }
}