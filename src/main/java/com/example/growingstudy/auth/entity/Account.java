package com.example.growingstudy.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    // 필드들은 대략적인 것으로, 나중에 변경될 수 있음

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // H2에서 AtomicLong 오류 나서 long으로 뒀음
    private String username;
    private String password;
    private String nickname;
}
