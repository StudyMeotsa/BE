package com.example.growingstudy.session.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checklist")
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(nullable = false)
    private String content; // content VARCHAR(255)

    @Column(nullable = false)
    private boolean completed; // completed BOOLEAN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session; // session_id BIGINT (FK)
//
//    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
//    private List<Submission> submissions = new ArrayList<>();

    @Builder
    private Checklist(String content, Session session) {
        this.content = content;
        this.session = session;
        this.completed = false; // 기본값
    }

    public static Checklist create(String content, Session session) {
        return new Checklist(content, session);
    }
}