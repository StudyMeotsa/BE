package com.checklist.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;       // 할 일 내용
    private boolean completed;    // 완료 여부
    private Long userId;          // 사용자 식별용 ID
}