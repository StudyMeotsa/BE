package com.example.growingstudy.studygroup.entity;

import com.example.growingstudy.auth.entity.Account;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //enum으로?
    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false, length = 30)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public GroupMember(String role, String nickname, StudyGroup group, Account account) {
        this.role = role;
        this.nickname = nickname;
        this.group = group;
        this.account = account;
    }
}
