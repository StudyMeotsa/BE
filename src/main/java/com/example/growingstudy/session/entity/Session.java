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
    private Long id; // id BIGINT (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // group_id BIGINT (FK)

    @Column(nullable = false)
    private String title; // title VARCHAR

    @Column(name = "session_order")
    private Integer sessionOrder; // session_order INT (주차/순서)

    public Session(Group group, String title, Integer sessionOrder) {
        this.group = group;
        this.title = title;
        this.sessionOrder = sessionOrder;
    }
}