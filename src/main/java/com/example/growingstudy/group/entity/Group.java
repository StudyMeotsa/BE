package com.example.growingstudy.group.entity;

import com.example.growingstudy.coffee.entity.Coffee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.example.growingstudy.auth.entity.Account;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 생성 시 입력 사항
    private String name;
    private Integer weekSession;
    private Integer totalWeek;
    private Integer maxMember;
    private Integer minHour;
    private String description;

    // UUID활용, 8자리
    private String code = UUID.randomUUID().toString().substring(0, 8);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id")
    private Account owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="coffee_id")
    private Coffee coffee;


// 생성하기 시
    public Group(String name, Integer weekSession, Integer totalWeek, Integer minHour, Integer maxMember, String description, Account account) {
        this.name = name;
        this.weekSession = weekSession;
        this.totalWeek = totalWeek;
        this.minHour = minHour;
        this.maxMember = maxMember;
        this.description = description;
        this.owner = account; // 로그인 정보 활용으로 변경 필요
    }
}
