package com.example.growingstudy.auth.repository;

import com.example.growingstudy.auth.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AccountRepository 단위 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryUnitTests {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("회원 저장 테스트")
    void testSave() {
        // given
        Account account = Account.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트유저")
                .sex("M")
                .imagePath("/images/test.jpg")
                .build();

        // when
        Account savedAccount = accountRepository.save(account);

        // then
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getEmail()).isEqualTo("test@example.com");
        assertThat(savedAccount.getPassword()).isEqualTo("password123");
        assertThat(savedAccount.getName()).isEqualTo("테스트유저");
        assertThat(savedAccount.getSex()).isEqualTo("M");
        assertThat(savedAccount.getImagePath()).isEqualTo("/images/test.jpg");
    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트 - 존재하는 경우")
    void testFindByEmailWhenExists() {
        // given
        Account account = Account.builder()
                .email("findtest@example.com")
                .password("password123")
                .name("조회테스트유저")
                .sex("F")
                .imagePath("/images/find.jpg")
                .build();
        accountRepository.save(account);

        // when
        Optional<Account> foundAccount = accountRepository.findByEmail("findtest@example.com");

        // then
        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getEmail()).isEqualTo("findtest@example.com");
        assertThat(foundAccount.get().getName()).isEqualTo("조회테스트유저");
    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트 - 존재하지 않는 경우")
    void testFindByEmailWhenNotExists() {
        // given
        String nonExistentEmail = "nonexistent@example.com";

        // when
        Optional<Account> foundAccount = accountRepository.findByEmail(nonExistentEmail);

        // then
        assertThat(foundAccount).isEmpty();
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인 테스트 - 존재하는 경우")
    void testExistsByEmailWhenExists() {
        // given
        Account account = Account.builder()
                .email("exists@example.com")
                .password("password123")
                .name("존재테스트유저")
                .sex("M")
                .imagePath("/images/exists.jpg")
                .build();
        accountRepository.save(account);

        // when
        boolean exists = accountRepository.existsByEmail("exists@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인 테스트 - 존재하지 않는 경우")
    void testExistsByEmailWhenNotExists() {
        // given
        String nonExistentEmail = "notexists@example.com";

        // when
        boolean exists = accountRepository.existsByEmail(nonExistentEmail);

        // then
        assertThat(exists).isFalse();
    }
}
