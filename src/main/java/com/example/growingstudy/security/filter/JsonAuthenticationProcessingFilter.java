package com.example.growingstudy.security.filter;

import com.example.growingstudy.security.dto.LoginRequestDto;
import com.example.growingstudy.security.util.ServletRequestConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class JsonAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final Logger logger = LoggerFactory.getLogger(JsonAuthenticationProcessingFilter.class);
    private final AuthenticationSuccessHandler successHandler;

    // 핸들러가 없을 경우 부모 클래스의 정의를 따름
    public JsonAuthenticationProcessingFilter(String defaultFilterProcessesUrl,
                                              AuthenticationManager authenticationManager) {
        this(defaultFilterProcessesUrl, authenticationManager, new SavedRequestAwareAuthenticationSuccessHandler());
    }

    // 시큐리티 설정에서 생성자 주입
    public JsonAuthenticationProcessingFilter(String defaultFilterProcessesUrl,
                                              AuthenticationManager authenticationManager,
                                              AuthenticationSuccessHandler successHandler) {
        super(defaultFilterProcessesUrl, authenticationManager);
        this.successHandler = successHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        logger.debug("인증 프로세스 시작");

        String jsonString = ServletRequestConverter.convertRequestToString(request);
        LoginRequestDto loginRequest = ServletRequestConverter.mapJsonToDto(jsonString, LoginRequestDto.class);

        String username = obtainUsername(loginRequest);
        String password = obtainPassword(loginRequest);

        UsernamePasswordAuthenticationToken token =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        Authentication result = this.getAuthenticationManager().authenticate(token);
        logger.debug(result.isAuthenticated()? "인증 성공" : "인증 실패");

        return result;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, chain, authResult);
    }

    protected String obtainUsername(LoginRequestDto loginRequestDto) {
        return loginRequestDto.getUsername();
    }

    protected String obtainPassword(LoginRequestDto loginRequestDto) {
        return loginRequestDto.getPassword();
    }
}
