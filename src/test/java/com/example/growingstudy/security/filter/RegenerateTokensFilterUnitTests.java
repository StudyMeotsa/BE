package com.example.growingstudy.security.filter;

import com.example.growingstudy.security.dto.JwtResponseDto;
import com.example.growingstudy.security.dto.RefreshOrLogoutRequestDto;
import com.example.growingstudy.security.service.JwtService;
import com.example.growingstudy.security.util.JsonResponseWriter;
import com.example.growingstudy.security.util.ServletRequestConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * RegenerateTokensFilter 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class RegenerateTokensFilterUnitTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RegenerateTokensFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RegenerateTokensFilter(jwtService);
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/refresh 요청은 필터 적용")
    void shouldNotFilterForRefreshRequestReturnsFalse() throws ServletException {
        // given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/api/auth/refresh");

        // when
        boolean result = filter.shouldNotFilter(mockRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldNotFilter - 그 외 요청은 필터 미적용")
    void shouldNotFilterForOtherRequestReturnsTrue() throws ServletException {
        // given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/api/other");

        // when
        boolean result = filter.shouldNotFilter(mockRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth 로 시작해도 그 외 요청은 필터 미적용")
    void shouldNotFilterForApiAuthOtherRequestReturnsTrue() throws ServletException {
        // given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/api/auth/other");

        // when
        boolean result = filter.shouldNotFilter(mockRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("doFilterInternal - 유효한 리프레쉬 토큰으로 토큰 재발급 성공")
    void doFilterInternalWithValidRefreshTokenRegeneratesTokens() throws ServletException, IOException {
        // given
        String refreshToken = "valid-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto requestDto = new RefreshOrLogoutRequestDto();
        requestDto.setRefreshToken(refreshToken);

        JwtResponseDto responseDto = new JwtResponseDto();
        responseDto.setAccessToken("new-access-token");
        responseDto.setRefreshToken("new-refresh-token");

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class);
             MockedStatic<JsonResponseWriter> mockedWriter = mockStatic(JsonResponseWriter.class)) {

            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(requestDto);

            given(jwtService.refreshTokens(refreshToken)).willReturn(responseDto);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(jwtService).refreshTokens(refreshToken);
            mockedWriter.verify(() -> JsonResponseWriter.writeResponseWithDto(response, responseDto));
            verify(response, never()).setStatus(anyInt());
        }
    }

    @Test
    @DisplayName("doFilterInternal - IllegalArgumentException 발생 시 400 응답")
    void doFilterInternalWithIllegalArgumentExceptionReturns400() throws ServletException, IOException {
        // given
        String refreshToken = "invalid-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto requestDto = new RefreshOrLogoutRequestDto();
        requestDto.setRefreshToken(refreshToken);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(requestDto);

            given(jwtService.refreshTokens(refreshToken))
                    .willThrow(new IllegalArgumentException("유효하지 않은 토큰"));

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Test
    @DisplayName("doFilterInternal - AuthenticationException 발생 시 401 응답")
    void doFilterInternalWithAuthenticationExceptionReturns401() throws ServletException, IOException {
        // given
        String refreshToken = "expired-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto requestDto = new RefreshOrLogoutRequestDto();
        requestDto.setRefreshToken(refreshToken);

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class)) {
            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(requestDto);

            given(jwtService.refreshTokens(refreshToken))
                    .willThrow(new BadCredentialsException("만료된 토큰"));

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Test
    @DisplayName("doFilterInternal - filterChain.doFilter 호출하지 않음")
    void doFilterInternalDoesNotContinueFilterChain() throws ServletException, IOException {
        // given
        String refreshToken = "valid-refresh-token";
        String requestBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        RefreshOrLogoutRequestDto requestDto = new RefreshOrLogoutRequestDto();
        requestDto.setRefreshToken(refreshToken);

        JwtResponseDto responseDto = new JwtResponseDto();
        responseDto.setAccessToken("new-access-token");
        responseDto.setRefreshToken("new-refresh-token");

        try (MockedStatic<ServletRequestConverter> mockedConverter = mockStatic(ServletRequestConverter.class);
             MockedStatic<JsonResponseWriter> mockedWriter = mockStatic(JsonResponseWriter.class)) {

            mockedConverter.when(() -> ServletRequestConverter.convertRequestToString(request))
                    .thenReturn(requestBody);
            mockedConverter.when(() -> ServletRequestConverter.mapJsonToDto(requestBody, RefreshOrLogoutRequestDto.class))
                    .thenReturn(requestDto);

            given(jwtService.refreshTokens(refreshToken)).willReturn(responseDto);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(filterChain, never()).doFilter(request, response);
        }
    }
}
