package com.example.growingstudy.studygroup.entity;

import com.example.growingstudy.auth.entity.Account;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ADMIN, MEMBER 나중에 ENUM으로 변경
    @Column(nullable = false, length = 30)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private GroupMember(String role, StudyGroup group, Account account) {
        this.role = role;
        this.group = group;
        this.account = account;
    }

    public static GroupMember of(String role, StudyGroup group, Account account) {
        return new GroupMember(role, group, account);
    }
}
