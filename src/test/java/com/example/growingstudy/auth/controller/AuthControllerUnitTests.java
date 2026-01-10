package com.example.growingstudy.auth.controller;

import com.example.growingstudy.auth.dto.MyPageResponseDto;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AuthController 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTests {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void testRegisterSuccess() {
        // given
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setName("테스트유저");
        request.setSex("M");

        doNothing().when(authService).register(any(RegisterRequestDto.class));

        // when
        ResponseEntity<Void> response = authController.register(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(authService, times(1)).register(request);
    }

    @Test
    @DisplayName("내 정보 조회 성공 테스트")
    void testMeSuccess() {
        // given
        long userId = 1L;
        Jwt mockJwt = createMockJwt(String.valueOf(userId));

        MyPageResponseDto expectedResponse = MyPageResponseDto.builder()
                .name("테스트유저")
                .sex("M")
                .email("test@example.com")
                .build();

        given(authService.retrieveUserInfo(userId)).willReturn(expectedResponse);

        // when
        ResponseEntity<MyPageResponseDto> response = authController.me(mockJwt);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("테스트유저");
        assertThat(response.getBody().getSex()).isEqualTo("M");
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
        verify(authService, times(1)).retrieveUserInfo(userId);
    }

    // 일관성 검증
    @Test
    @DisplayName("내 정보 조회 테스트 - 다른 유저 ID")
    void testMeWithDifferentUserId() {
        // given
        long userId = 100L;
        Jwt mockJwt = createMockJwt(String.valueOf(userId));

        MyPageResponseDto expectedResponse = MyPageResponseDto.builder()
                .name("다른유저")
                .sex("F")
                .email("other@example.com")
                .build();

        given(authService.retrieveUserInfo(userId)).willReturn(expectedResponse);

        // when
        ResponseEntity<MyPageResponseDto> response = authController.me(mockJwt);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("다른유저");
        assertThat(response.getBody().getSex()).isEqualTo("F");
        assertThat(response.getBody().getEmail()).isEqualTo("other@example.com");
        verify(authService, times(1)).retrieveUserInfo(userId);
    }

    /**
     * 테스트용 Mock Jwt 객체 생성
     * @param subject JWT의 subject (userId)
     * @return Mock Jwt 객체
     */
    private Jwt createMockJwt(String subject) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);

        return new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims
        );
    }
}
