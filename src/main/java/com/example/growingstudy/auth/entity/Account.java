package com.example.growingstudy.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // H2에서 AtomicLong 오류 나서 long으로 뒀음
    private String username;
    private String email;
    private String password;
    private String sex;
    private String image;

    public Account(String username, String email, String password, String sex) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.sex = sex;
    }
}
