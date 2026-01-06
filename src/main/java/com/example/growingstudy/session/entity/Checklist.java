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

    @Column(nullable = false, length = 255)
    private String content; // content VARCHAR(255)

    @Column(columnDefinition = "TEXT")
    private String description; // description TEXT

    @Column(nullable = false)
    private boolean completed = false; // completed BOOLEAN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session; // session_id BIGINT (FK)

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    @Builder
    public Checklist(String content, String description, Session session) {
        this.content = content;
        this.description = description;
        this.session = session;
        this.completed = false; // 기본값
    }
}