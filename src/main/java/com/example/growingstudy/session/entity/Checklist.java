package com.example.growingstudy.session.entity;

import com.example.growingstudy.studygroup.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checklist")
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(name = "content", nullable = false)
    private String content; // content VARCHAR

    @Column(columnDefinition = "TEXT")
    private String description; // description TEXT

    @Column(name = "completed", nullable = false)
    private boolean completed = false; // completed BOOLEAN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group; // group_id BIGINT (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session; // session_id BIGINT (FK)

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    public Checklist(String content, String description, StudyGroup group, Session session) {
        this.content = content;
        this.description = description;
        this.group = group;
        this.session = session;
    }
}