package com.example.growingstudy.session.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checklist")
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(nullable = false)
    private String title; // title VARCHAR(255)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed; // completed BOOLEAN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session; // session_id BIGINT (FK)
//
//    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
//    private List<Submission> submissions = new ArrayList<>();

    @Builder
    private Checklist(String title, String description, Session session) {
        this.title = title;
        this.description = description;
        this.session = session;
        this.completed = false; // 기본값
    }

    public static Checklist create(String content, String description, Session session) {
        return new Checklist(content, description, session);
    }
}