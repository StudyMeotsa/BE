package com.checklist.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;       // 할 일 내용

    @Column(nullable = false)
    private boolean completed;    // 완료 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User 엔티티와 관계 매핑

    @Builder
    public Checklist(String content, User user) {
        this.content = content;
        this.completed = false; // 생성 시 항상 false로 초기화
        this.user = user;
    }

    /**
     * 체크리스트를 완료 상태로 변경하는 비즈니스 메서드
     */
    public void complete() {
        if (!this.completed) {
            this.completed = true;
        }
    }

    /**
     * 체크리스트 내용을 수정하는 비즈니스 메서드
     */
    public void updateContent(String content) {
        this.content = content;
    }
}