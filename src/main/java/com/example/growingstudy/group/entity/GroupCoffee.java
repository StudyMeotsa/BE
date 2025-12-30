package com.example.growingstudy.group.entity;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.coffee.entity.CoffeeLevel;
import com.example.growingstudy.coffee.entity.CoffeeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private CoffeeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level", nullable = false)
    private CoffeeLevel level;

    @Column(nullable = false)
    private Integer currentBeans;
}
