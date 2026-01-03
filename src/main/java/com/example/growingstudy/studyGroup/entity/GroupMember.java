package com.example.growingstudy.studyGroup.entity;

import com.example.growingstudy.auth.entity.Account;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
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
