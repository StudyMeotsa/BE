package com.example.growingstudy.security.filter;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * CheckAccessTokenFilter 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class CheckAccessTokenFilterUnitTests {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    private CheckAccessTokenFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CheckAccessTokenFilter();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/login 요청은 필터 미적용")
    void shouldNotFilterForLoginRequest() throws ServletException {
        // given
        given(request.getRequestURI()).willReturn("/api/auth/login");

        // when
        boolean result = filter.shouldNotFilter(request);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/refresh 요청은 필터 미적용")
    void shouldNotFilterForRefreshRequest() throws ServletException {
        // given
        given(request.getRequestURI()).willReturn("/api/auth/refresh");

        // when
        boolean result = filter.shouldNotFilter(request);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/register 요청은 필터 미적용")
    void shouldNotFilterForRegisterRequest() throws ServletException {
        // given
        given(request.getRequestURI()).willReturn("/api/auth/register");

        // when
        boolean result = filter.shouldNotFilter(request);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/logout 요청은 필터 미적용")
    void shouldNotFilterForLogoutRequest() throws ServletException {
        // given
        given(request.getRequestURI()).willReturn("/api/auth/logout");

        // when
        boolean result = filter.shouldNotFilter(request);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/me 요청은 필터 적용")
    void shouldNotFilterForMeRequestReturnsFalse() throws ServletException {
        // given
        given(request.getRequestURI()).willReturn("/api/auth/me");

        // when
        boolean result = filter.shouldNotFilter(request);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth/mycoffees 요청은 필터 적용")
    void shouldNotFilterForMycoffeesRequestReturnsFalse() throws ServletException {
        // given
        given(request.getRequestURI()).willReturn("/api/auth/mycoffees");

        // when
        boolean result = filter.shouldNotFilter(request);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldNotFilter - /api/auth 프리픽스가 아닌 요청은 필터 적용")
    void shouldNotFilterForNonAuthPrefixRequestReturnsFalse() throws ServletException {
        // given
        given(request.getRequestURI()).willReturn("/api/studygroups");

        // when
        boolean result = filter.shouldNotFilter(request);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("doFilterInternal - type이 access인 토큰은 필터 체인 계속 진행")
    void doFilterInternalWithAccessTokenContinuesFilterChain() throws ServletException, IOException {
        // given
        given(jwt.getClaim("type")).willReturn("access");

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(jwt);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
            verify(response, never()).setStatus(anyInt());
        }
    }

    @Test
    @DisplayName("doFilterInternal - type이 refresh인 토큰은 401 응답")
    void doFilterInternalWithRefreshTokenReturns401() throws ServletException, IOException {
        // given
        given(jwt.getClaim("type")).willReturn("refresh");

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(jwt);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("doFilterInternal - type claim이 null인(없는) 토큰은 401 응답")
    void doFilterInternalWithNullTypeClaimReturns401() throws ServletException, IOException {
        // given
        given(jwt.getClaim("type")).willReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(jwt);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("doFilterInternal - Authentication이 null이면 401 응답")
    void doFilterInternalWithNullAuthenticationReturns401() throws ServletException, IOException {
        // given
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(null);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("doFilterInternal - Principal이 null이면 401 응답")
    void doFilterInternalWithNullPrincipalReturns401() throws ServletException, IOException {
        // given
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(null);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("doFilterInternal - Principal이 Jwt 타입이 아니면 401 응답")
    void doFilterInternalWithNonJwtPrincipalReturns401() throws ServletException, IOException {
        // given
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn("not-a-jwt-object");

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(filterChain, never()).doFilter(request, response);
        }
    }
}
