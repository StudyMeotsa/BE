package com.example.growingstudy.studyGroup.entity;

import com.example.growingstudy.coffee.entity.CoffeeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupCoffee {
    @Id
    private Long groupId;

    // pk = fk
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private CoffeeType type;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer currentBeans;
}
