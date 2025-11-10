package com.evaluation.entity;

import com.checklist.entity.Checklist;
import com.checklist.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int score;

    @Column(length = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false, unique = true)
    private Checklist checklist;

    @Builder
    public Evaluation(int score, String comment, User user, Checklist checklist) {
        this.score = score;
        this.comment = comment;
        this.user = user;
        this.checklist = checklist;
    }
}