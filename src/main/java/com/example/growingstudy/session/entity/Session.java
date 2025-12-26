package com.example.growingstudy.session.entity;

import com.example.growingstudy.group.entity.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 세션 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // 연결된 Group ID

    @Column(nullable = false)
    private String title; // 세션 제목

    private Integer week; // 주차

    public Session(Group group, String title, Integer week) {
        this.group = group;
        this.title = title;
        this.week = week;
    }
}