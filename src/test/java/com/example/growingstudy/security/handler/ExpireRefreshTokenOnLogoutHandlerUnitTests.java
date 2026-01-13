package com.example.growingstudy.security.handler;

import com.example.growingstudy.security.dto.RefreshOrLogoutRequestDto;
import com.example.growingstudy.security.service.JwtService;
import com.example.growingstudy.security.util.ServletRequestConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockHttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * ExpireRefreshTokenOnLogoutHandler 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ExpireRefreshTokenOnLogoutHandlerUnitTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private ExpireRefreshTokenOnLogoutHandler logoutHandler;

    @BeforeEach
    void setUp() {
        logoutHandler = new ExpireRefreshTokenOnLogoutHandler(jwtService);
    }

    @Test
    @DisplayName("logout - 정상적으로 리프레쉬 토큰 소모")
    void testLogoutSuccess() throws Exception {
        // given
        String refreshToken = "valid-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto dto = new RefreshOrLogoutRequestDto();
        dto.setRefreshToken(refreshToken);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(dto);

            // when
            logoutHandler.logout(request, response, authentication);

            // then
            verify(jwtService).consumeRefreshToken(refreshToken);
            verify(response, never()).setStatus(anyInt());
        }
    }

    // 일관성 확인
    @Test
    @DisplayName("logout - 다른 리프레쉬 토큰으로 정상 소모")
    void testLogoutWithDifferentRefreshToken() throws Exception {
        // given
        String refreshToken = "another-valid-refresh-token-12345";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto dto = new RefreshOrLogoutRequestDto();
        dto.setRefreshToken(refreshToken);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(dto);

            // when
            logoutHandler.logout(request, response, authentication);

            // then
            verify(jwtService).consumeRefreshToken(refreshToken);
        }
    }

    @Test
    @DisplayName("logout - IllegalArgumentException 발생 시 400 응답")
    void testLogoutWithIllegalArgumentException() throws Exception {
        // given
        String refreshToken = "invalid-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto dto = new RefreshOrLogoutRequestDto();
        dto.setRefreshToken(refreshToken);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(dto);

            given(jwtService.consumeRefreshToken(refreshToken))
                    .willThrow(new IllegalArgumentException("리프레쉬 토큰이 아님"));

            // when
            logoutHandler.logout(request, response, authentication);

            // then
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(response).flushBuffer();
        }
    }

    @Test
    @DisplayName("logout - AuthenticationException 발생 시 401 응답")
    void testLogoutWithAuthenticationException() throws Exception {
        // given
        String refreshToken = "expired-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto dto = new RefreshOrLogoutRequestDto();
        dto.setRefreshToken(refreshToken);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(dto);

            given(jwtService.consumeRefreshToken(refreshToken))
                    .willThrow(new OAuth2AuthenticationException("Token expired"));

            // when
            logoutHandler.logout(request, response, authentication);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).flushBuffer();
        }
    }

    @Test
    @DisplayName("logout - IOException 발생 시 RuntimeException 발생")
    void testLogoutWithIOException() throws Exception {
        // given
        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenThrow(new java.io.IOException("IO error"));

            // when & then
            assertThatThrownBy(() -> logoutHandler.logout(request, response, authentication))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(java.io.IOException.class);
        }
    }

    @Test
    @DisplayName("writeUnmodifiableResponseStatusCode - 응답 코드 설정 후 커밋되어 변경 불가")
    void testResponseStatusCodeIsCommittedAfterFlush() throws Exception {
        // given
        String refreshToken = "invalid-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto dto = new RefreshOrLogoutRequestDto();
        dto.setRefreshToken(refreshToken);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(dto);

            given(jwtService.consumeRefreshToken(refreshToken))
                    .willThrow(new IllegalArgumentException("리프레쉬 토큰이 아님"));

            // when
            logoutHandler.logout(request, mockResponse, authentication);

            // then
            assertThat(mockResponse.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
            assertThat(mockResponse.isCommitted()).isTrue();

            // 커밋 후 상태 코드 변경 시도해도 변경되지 않음
            mockResponse.setStatus(HttpServletResponse.SC_OK);
            assertThat(mockResponse.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
