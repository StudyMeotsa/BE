package com.evaluation.entity;

import com.checklist.entity.Checklist;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;
    private String comment;
    private Long userId;

    @OneToOne
    @JoinColumn(name = "checklist_id")
    private Checklist checklist;
}