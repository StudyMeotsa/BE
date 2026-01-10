package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.MyPageResponseDto;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.enums.RegisterFailedType;
import com.example.growingstudy.auth.exception.RegisterFailedException;
import com.example.growingstudy.auth.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AuthService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTests {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 테스트 - 중복 유저 없는 경우")
    void testRegisterSuccessWhenNoUserDuplicate() {
        // given
        AuthService spyAuthService = spy(new AuthService(passwordEncoder, accountRepository));

        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("테스트유저");
        request.setSex("M");

        doNothing().when(spyAuthService).validateUniqueness(any(RegisterRequestDto.class));
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(accountRepository.save(any(Account.class))).willReturn(Account.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("테스트유저")
                .sex("M")
                .build());

        // when
        spyAuthService.register(request);

        // then
        verify(spyAuthService, times(1)).validateUniqueness(request);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복 유저 있는 경우")
    void testRegisterFailureWhenUserDuplicate() {
        // given
        AuthService spyAuthService = spy(new AuthService(passwordEncoder, accountRepository));

        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("중복유저");
        request.setSex("F");

        doThrow(new RegisterFailedException(RegisterFailedType.USERNAME_NOT_UNIQUE))
                .when(spyAuthService).validateUniqueness(any(RegisterRequestDto.class));

        // when & then
        assertThatThrownBy(() -> spyAuthService.register(request))
                .isInstanceOf(RegisterFailedException.class)
                .hasMessage(RegisterFailedType.USERNAME_NOT_UNIQUE.getMessage())
                .extracting(ex -> ((RegisterFailedException) ex).getType())
                .isEqualTo(RegisterFailedType.USERNAME_NOT_UNIQUE);

        verify(spyAuthService, times(1)).validateUniqueness(request);
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("유효성 검증 성공 테스트 - 중복이 없는 경우")
    void testValidateUniquenessSuccess() {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("unique@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("유니크유저");
        request.setSex("F");

        given(accountRepository.existsByEmail(anyString())).willReturn(false);

        // when
        authService.validateUniqueness(request);

        // then
        verify(accountRepository, times(1)).existsByEmail("unique@example.com");
    }

    @Test
    @DisplayName("유효성 검증 실패 테스트 - 이메일 중복인 경우")
    void testValidateUniquenessFailureWhenEmailDuplicate() {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("중복유저");
        request.setSex("M");

        given(accountRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.validateUniqueness(request))
                .isInstanceOf(RegisterFailedException.class)
                .hasMessage(RegisterFailedType.USERNAME_NOT_UNIQUE.getMessage())
                .extracting(ex -> ((RegisterFailedException) ex).getType())
                .isEqualTo(RegisterFailedType.USERNAME_NOT_UNIQUE);

        verify(accountRepository, times(1)).existsByEmail("duplicate@example.com");
    }

    @Test
    @DisplayName("유저 정보 조회 성공 테스트")
    void testRetrieveUserInfoSuccess() {
        // given
        long userId = 1L;
        Account account = Account.builder()
                .id(userId)
                .email("info@example.com")
                .password("encodedPassword")
                .name("정보조회유저")
                .sex("F")
                .imagePath("/images/user.jpg")
                .build();

        given(accountRepository.findById(userId)).willReturn(Optional.of(account));

        // when
        MyPageResponseDto result = authService.retrieveUserInfo(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("정보조회유저");
        assertThat(result.getSex()).isEqualTo("F");
        assertThat(result.getEmail()).isEqualTo("info@example.com");

        verify(accountRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("유저 정보 조회 실패 테스트 - 유저가 존재하지 않는 경우")
    void testRetrieveUserInfoFailureWhenUserNotFound() {
        // given
        long nonExistentUserId = 999L;
        given(accountRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.retrieveUserInfo(nonExistentUserId))
                .isInstanceOf(NoSuchElementException.class);

        verify(accountRepository, times(1)).findById(nonExistentUserId);
    }
}
