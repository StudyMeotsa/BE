package com.example.growingstudy.security.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.security.entity.UserDetailsWithId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * AccountRepositoryUserDetailsService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class AccountRepositoryUserDetailsServiceUnitTests {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountRepositoryUserDetailsService userDetailsService;

    @Test
    @DisplayName("존재하는 이메일로 조회 시 UserDetails 반환")
    void testLoadUserByUsernameWhenEmailExists() {
        // given
        String email = "test@example.com";
        Account account = Account.builder()
                .id(1L)
                .email(email)
                .password("encodedPassword123")
                .name("테스트유저")
                .sex("M")
                .imagePath("/images/test.jpg")
                .build();

        given(accountRepository.findByEmail(email)).willReturn(Optional.of(account));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(UserDetailsWithId.class);
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword123");

        UserDetailsWithId userDetailsWithId = (UserDetailsWithId) userDetails;
        assertThat(userDetailsWithId.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 UsernameNotFoundException 발생")
    void testLoadUserByUsernameWhenEmailNotExists() {
        // given
        String nonExistentEmail = "nonexistent@example.com";

        given(accountRepository.findByEmail(nonExistentEmail)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(nonExistentEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(nonExistentEmail);
    }

    @Test
    @DisplayName("조회 후 Account의 비밀번호가 null로 설정됨")
    void testLoadUserByUsernameNullifiesAccountPassword() {
        // given
        String email = "password-test@example.com";
        Account account = Account.builder()
                .id(2L)
                .email(email)
                .password("shouldBeNullified")
                .name("비밀번호테스트")
                .sex("F")
                .build();

        given(accountRepository.findByEmail(email)).willReturn(Optional.of(account));

        // when
        userDetailsService.loadUserByUsername(email);

        // then
        assertThat(account.getPassword()).isNull();
    }
}
