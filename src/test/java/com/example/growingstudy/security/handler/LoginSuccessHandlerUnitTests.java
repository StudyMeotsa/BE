package com.example.growingstudy.security.handler;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.entity.UserDetailsWithId;
import com.example.growingstudy.security.service.JwtService;
import com.example.growingstudy.security.util.JsonResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * LoginSuccessHandler 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class LoginSuccessHandlerUnitTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    private LoginSuccessHandler loginSuccessHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        loginSuccessHandler = new LoginSuccessHandler(jwtService);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 로그인 성공 시 JWT 토큰 응답")
    void testOnAuthenticationSuccessWithFilterChain() throws Exception {
        // given
        long userId = 1L;
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("test@example.com")
                .password("password")
                .userId(userId)
                .build();

        JwtResponseDto jwtResponseDto = new JwtResponseDto();
        jwtResponseDto.setAccessToken("access-token-value");
        jwtResponseDto.setRefreshToken("refresh-token-value");

        given(authentication.getPrincipal()).willReturn(userDetails);
        given(jwtService.generateTokens(userId)).willReturn(jwtResponseDto);

        try (MockedStatic<JsonResponseWriter> mockedWriter = mockStatic(JsonResponseWriter.class)) {
            // when
            loginSuccessHandler.onAuthenticationSuccess(request, response, filterChain, authentication);

            // then
            verify(authentication).getPrincipal();
            verify(jwtService).generateTokens(userId);
            mockedWriter.verify(() ->
                    JsonResponseWriter.writeResponseWithDto(response, jwtResponseDto));
        }
    }

    // 일관성 확인
    @Test
    @DisplayName("onAuthenticationSuccess - 다른 유저 ID로 로그인 성공")
    void testOnAuthenticationSuccessWithDifferentUserId() throws Exception {
        // given
        long userId = 999L;
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("another@example.com")
                .password("password123")
                .userId(userId)
                .build();

        JwtResponseDto jwtResponseDto = new JwtResponseDto();
        jwtResponseDto.setAccessToken("another-access-token");
        jwtResponseDto.setRefreshToken("another-refresh-token");

        given(authentication.getPrincipal()).willReturn(userDetails);
        given(jwtService.generateTokens(userId)).willReturn(jwtResponseDto);

        try (MockedStatic<JsonResponseWriter> mockedWriter = mockStatic(JsonResponseWriter.class)) {
            // when
            loginSuccessHandler.onAuthenticationSuccess(request, response, filterChain, authentication);

            // then
            verify(jwtService).generateTokens(userId);
            mockedWriter.verify(() ->
                    JsonResponseWriter.writeResponseWithDto(response, jwtResponseDto));
        }
    }

    @Test
    @DisplayName("onAuthenticationSuccess - 실제 응답 작성 검증")
    void testOnAuthenticationSuccessWritesActualResponse() throws Exception {
        // given
        long userId = 1L;
        UserDetailsWithId userDetails = UserDetailsWithId.builder()
                .username("test@example.com")
                .password("password")
                .userId(userId)
                .build();

        JwtResponseDto jwtResponseDto = new JwtResponseDto();
        jwtResponseDto.setAccessToken("real-access-token");
        jwtResponseDto.setRefreshToken("real-refresh-token");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        given(authentication.getPrincipal()).willReturn(userDetails);
        given(jwtService.generateTokens(userId)).willReturn(jwtResponseDto);
        given(response.getWriter()).willReturn(printWriter);

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, filterChain, authentication);

        // then
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(200);

        String expectedJson = objectMapper.writeValueAsString(jwtResponseDto);
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }
}
