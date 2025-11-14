package com.checklist.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT") 
    private String description; 

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>(); 

    private LocalDateTime startTime; 
    private LocalDateTime endTime;   

    @Builder
    public Checklist(String content, String description, User user) {
        this.content = content;
        this.description = description;
        this.completed = false;
        this.user = user;
    }

    // --- 엔티티 비즈니스 로직 ---
    public void updateContent(String content) { this.content = content; }
    public void updateDescription(String description) { this.description = description; }
    public void complete() { this.completed = true; } 

    public void startSession() { 
        if (this.startTime == null) { this.startTime = LocalDateTime.now(); }
    }

    public void endSession() {
        if (this.endTime == null && this.startTime != null) { this.endTime = LocalDateTime.now(); }
    }
}