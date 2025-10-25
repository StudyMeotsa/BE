package com.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;           // 평가 점수 (예: 1~5)
    private String comment;      // 평가 코멘트
    private Long userId;         // 평가한 사용자 ID

    @OneToOne
    private Checklist checklist; // 평가 대상 체크리스트
}