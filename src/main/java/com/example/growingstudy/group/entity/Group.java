package com.example.growingstudy.group.entity;

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
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 생성 시 입력 사항
    private String name;
    private String description;
    private String weekSession;
    private String totalSession;
    private String minHour;
    private String maxMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id")
    private Account owner;

    // UUID활용, 8자리
    private String code = UUID.randomUUID().toString().substring(0, 8);

    public Group(String name, String weekSession, String totalSession, String minHour, String maxMember, Account account, String code) {
        this.name = name;
        this.weekSession = weekSession;
        this.totalSession = totalSession;
        this.minHour = minHour;
        this.maxMember = maxMember;
        this.owner = account; // 로그인 정보 활용으로 변경 필요
    }
}
