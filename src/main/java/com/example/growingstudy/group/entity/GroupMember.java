package com.example.growingstudy.group.entity;

import com.example.growingstudy.auth.entity.Account;
import jakarta.persistence.*;

@Entity
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_account_id")
    private Account member;

    // *필요하다면*: 그룹 내 역할 정의 (예: ADMIN, MEMBER)
    // private String roleInGroup;
}
