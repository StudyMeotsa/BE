package com.example.growingstudy.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // H2에서 AtomicLong 오류 나서 long으로 뒀음

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 10, nullable = false)
    private String sex;

    @Column(name = "image_path")
    private String imagePath;

    public Account(String name, String email, String password, String sex) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.sex = sex;
    }
}
