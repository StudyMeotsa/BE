package com.example.growingstudy.security.filter;

import com.example.growingstudy.auth.dto.LoginRequestDto;
import com.example.growingstudy.security.handler.LoginSuccessHandler;
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

import java.io.IOException;

public class JsonAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final Logger logger = LoggerFactory.getLogger(JsonAuthenticationProcessingFilter.class);
    private final ServletRequestConverter converter = new ServletRequestConverter();
    private final AuthenticationSuccessHandler successHandler = new LoginSuccessHandler();

    public JsonAuthenticationProcessingFilter(String defaultFilterProcessesUrl,
                                              AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        logger.info("인증 프로세스 시작");
        try {
            String jsonString = converter.convertRequestToString(request);
            LoginRequestDto loginRequest = converter.mapJsonToDto(jsonString, LoginRequestDto.class);

            String username = obtainUsername(loginRequest);
            String password = obtainPassword(loginRequest);

            UsernamePasswordAuthenticationToken token =
                    UsernamePasswordAuthenticationToken.unauthenticated(username, password);

            Authentication result = this.getAuthenticationManager().authenticate(token);
            logger.info(result.isAuthenticated()? "인증 성공" : "인증 실패");

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
