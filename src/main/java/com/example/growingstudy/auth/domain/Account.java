package com.example.growingstudy.auth.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Account")
public class Account {

    // 필드들은 대략적인 것으로, 나중에 변경될 수 있음

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // H2에서 AtomicLong 오류 나서 long으로 뒀음
    private String username;
    private String password;
    private String nickname;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
