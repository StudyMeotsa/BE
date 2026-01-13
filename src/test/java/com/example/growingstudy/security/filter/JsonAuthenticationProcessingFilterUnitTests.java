package com.example.growingstudy.security.filter;

import com.example.growingstudy.security.dto.LoginRequestDto;
import com.example.growingstudy.security.util.ServletRequestConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * JsonAuthenticationProcessingFilter 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class JsonAuthenticationProcessingFilterUnitTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticationSuccessHandler successHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    private JsonAuthenticationProcessingFilter filter;

    private static final String LOGIN_URL = "/api/auth/login";

    @BeforeEach
    void setUp() {
        filter = new JsonAuthenticationProcessingFilter(LOGIN_URL, authenticationManager, successHandler);
    }

    @Test
    @DisplayName("attemptAuthentication - 정상적인 로그인 요청 처리")
    void testAttemptAuthenticationSuccess() throws Exception {
        // given
        String email = "test@example.com";
        String password = "password123";
        String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);

        Authentication expectedAuth = UsernamePasswordAuthenticationToken.authenticated(
                email, password, java.util.Collections.emptyList());

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, LoginRequestDto.class))
                    .thenReturn(dto);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(expectedAuth);

            // when
            Authentication result = filter.attemptAuthentication(request, response);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isAuthenticated()).isTrue();
            assertThat(result.getPrincipal()).isEqualTo(email);
        }
    }

    @Test
    @DisplayName("attemptAuthentication - AuthenticationManager에 올바른 토큰 전달")
    void testAttemptAuthenticationPassesCorrectToken() throws Exception {
        // given
        String email = "user@example.com";
        String password = "secret";
        String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, LoginRequestDto.class))
                    .thenReturn(dto);

            given(authenticationManager.authenticate(tokenCaptor.capture()))
                    .willReturn(authentication);

            // when
            filter.attemptAuthentication(request, response);

            // then
            UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
            assertThat(capturedToken.getPrincipal()).isEqualTo(email);
            assertThat(capturedToken.getCredentials()).isEqualTo(password);
            assertThat(capturedToken.isAuthenticated()).isFalse();
        }
    }

    @Test
    @DisplayName("successfulAuthentication - 성공 핸들러 호출")
    void testSuccessfulAuthenticationCallsHandler() throws Exception {
        // when
        filter.successfulAuthentication(request, response, filterChain, authentication);

        // then
        verify(successHandler).onAuthenticationSuccess(request, response, filterChain, authentication);
    }

    @Test
    @DisplayName("successfulAuthentication - 핸들러에 올바른 인자 전달")
    void testSuccessfulAuthenticationPassesCorrectArguments() throws Exception {
        // given
        // when
        filter.successfulAuthentication(request, response, filterChain, authentication);

        // then
        verify(successHandler, times(1)).onAuthenticationSuccess(
                same(request),
                same(response),
                same(filterChain),
                same(authentication)
        );
    }

    @Test
    @DisplayName("attemptAuthentication - 잘못된 자격 증명(비밀번호 다름) 시 예외 발생 및 성공 핸들러 미호출")
    void testAttemptAuthenticationWithInvalidCredentials() throws Exception {
        // given
        String email = "test@example.com";
        String password = "wrongpassword";
        String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, LoginRequestDto.class))
                    .thenReturn(dto);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willThrow(new BadCredentialsException("자격 증명 실패"));

            // when & then
            assertThatThrownBy(() -> filter.attemptAuthentication(request, response))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("자격 증명 실패");

            // 성공 핸들러가 호출되지 않았는지 검증
            verifyNoInteractions(successHandler);
        }
    }

    @Test
    @DisplayName("attemptAuthentication - 존재하지 않는 사용자로 인증 시도 시 성공 핸들러 미호출")
    void testAttemptAuthenticationWithNonExistentUser() throws Exception {
        // given
        String email = "nonexistent@example.com";
        String password = "password";
        String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, LoginRequestDto.class))
                    .thenReturn(dto);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willThrow(new BadCredentialsException("사용자를 찾을 수 없음"));

            // when & then
            assertThatThrownBy(() -> filter.attemptAuthentication(request, response))
                    .isInstanceOf(BadCredentialsException.class);

            // 성공 핸들러가 호출되지 않았는지 검증
            verifyNoInteractions(successHandler);
        }
    }
}
