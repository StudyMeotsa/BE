package com.example.growingstudy.groupsub.entity;

import com.example.growingstudy.coffee.entity.CoffeeType;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_coffee")
public class GroupCoffee {
    @Id
    private Long id;

    // pk = fk
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private CoffeeType type;

    @Column(name = "required_all", nullable = false)
    private Integer requiredAll;

    @Column(name = "required_per_level", nullable = false)
    private Integer requiredPerLevel;

    @Column(nullable = false)
    private Integer current;

    @Column(nullable = false)
    private Integer level;

    public GroupCoffee(StudyGroup group, CoffeeType type, Integer requiredAll, Integer requiredPerLevel, Integer current, Integer level) {
        this.group = group;
        this.type = type;
        this.requiredAll = requiredAll;
        this.requiredPerLevel = requiredPerLevel;
        this.current = current;
        this.level = level;
    }
}
