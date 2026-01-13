package com.example.growingstudy.security.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.security.entity.UserDetailsWithId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AccountRepositoryUserDetailsService 스프링 부트 통합 테스트
 */
@SpringBootTest
class AccountRepositoryUserDetailsServiceSpringIntegrationTests {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testEmail;
    private Long savedAccountId;

    @BeforeEach
    void setUp() {
        testEmail = "test-" + UUID.randomUUID() + "@example.com";
    }

    @AfterEach
    void tearDown() {
        if (savedAccountId != null) {
            accountRepository.deleteById(savedAccountId);
            savedAccountId = null;
        }
    }

    @Test
    @DisplayName("loadUserByUsername - 존재하는 이메일로 조회 시 UserDetails 반환")
    void loadUserByUsernameWhenEmailExists() {
        // given
        String rawPassword = "testPassword123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Account account = Account.builder()
                .email(testEmail)
                .password(encodedPassword)
                .name("테스트유저")
                .sex("M")
                .imagePath("/images/test.jpg")
                .build();

        Account savedAccount = accountRepository.save(account);
        savedAccountId = savedAccount.getId();

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(UserDetailsWithId.class);
        assertThat(userDetails.getUsername()).isEqualTo(testEmail);
        assertThat(userDetails.getPassword()).isEqualTo(encodedPassword);

        UserDetailsWithId userDetailsWithId = (UserDetailsWithId) userDetails;
        assertThat(userDetailsWithId.getUserId()).isEqualTo(savedAccountId);
    }

    @Test
    @DisplayName("loadUserByUsername - 존재하지 않는 이메일로 조회 시 UsernameNotFoundException 발생")
    void loadUserByUsernameWhenEmailNotExists() {
        // given
        String nonExistentEmail = "nonexistent-" + UUID.randomUUID() + "@example.com";

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(nonExistentEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(nonExistentEmail);
    }

    @Test
    @DisplayName("loadUserByUsername - UserDetailsWithId가 올바른 userId를 포함")
    void loadUserByUsernameReturnsCorrectUserId() {
        // given
        Account account = Account.builder()
                .email(testEmail)
                .password(passwordEncoder.encode("password"))
                .name("유저ID테스트")
                .sex("F")
                .build();

        Account savedAccount = accountRepository.save(account);
        savedAccountId = savedAccount.getId();

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // then
        assertThat(userDetails).isInstanceOf(UserDetailsWithId.class);
        UserDetailsWithId userDetailsWithId = (UserDetailsWithId) userDetails;
        assertThat(userDetailsWithId.getUserId()).isEqualTo(savedAccountId);
    }

    @Test
    @DisplayName("loadUserByUsername - 반환된 비밀번호가 인코딩된 상태로 유지됨")
    void loadUserByUsernameReturnsEncodedPassword() {
        // given
        String rawPassword = "rawPassword123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Account account = Account.builder()
                .email(testEmail)
                .password(encodedPassword)
                .name("비밀번호테스트")
                .sex("F")
                .build();

        Account savedAccount = accountRepository.save(account);
        savedAccountId = savedAccount.getId();

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // then
        assertThat(userDetails.getPassword()).isEqualTo(encodedPassword);
        assertThat(userDetails.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, userDetails.getPassword())).isTrue();
    }
}
