package com.example.growingstudy.studyGroup.entity;

import com.example.growingstudy.coffee.entity.CoffeeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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
    private Integer requiredAll;

    @Column(nullable = false)
    private Integer requiredPerLevel;

    @Column(nullable = false)
    private Integer cureent;

    @Column(nullable = false)
    private Integer level;

    public GroupCoffee(StudyGroup group, CoffeeType type, Integer requiredAll, Integer requiredPerLevel, Integer cureent, Integer level) {
        this.group = group;
        this.type = type;
        this.requiredAll = requiredAll;
        this.requiredPerLevel = requiredPerLevel;
        this.cureent = cureent;
        this.level = level;
    }
}
