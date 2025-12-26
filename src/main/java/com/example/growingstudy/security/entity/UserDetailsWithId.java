package com.example.growingstudy.security.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsWithId implements UserDetails, CredentialsContainer {

    private final String username;
    private String password;
    private final long userId; // Account의 id

    // 최대한 기존 User와 스펙 일관성 유지 위해 빌더 사용
    @Builder
    private UserDetailsWithId(String username, String password, long userId) {
        this.username = username;
        this.password = password;
        this.userId = userId;
    }

    // 인증 후 비밀번호 지우기
    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    // 사용 안하는데 UserDetails 스펙상 존재해야 함
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
