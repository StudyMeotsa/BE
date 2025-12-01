package com.checklist.entity;

import com.group.entity.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String description;
    private boolean completed;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public void complete() { this.completed = true; }
    public void startSession() { this.startTime = LocalDateTime.now(); }
    public void endSession() { this.endTime = LocalDateTime.now(); }
}
