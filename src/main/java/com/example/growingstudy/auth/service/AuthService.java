package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.JwtResponseDto;
import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.enums.RegisterFailedType;
import com.example.growingstudy.auth.exception.RegisterFailedException;
import com.example.growingstudy.auth.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    private final JwtService jwtService;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, AccountRepository accountRepository, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequestDto request) {
        logger.debug("회원가입 서비스 로직 시작");

        logger.trace("회원가입 유효성을 검증");
        validateUniqueness(request);
        logger.trace("유효성 검증 성공");
        logger.trace("새 회원 데이터를 생성");
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setNickname(request.getNickname());
        logger.trace("새 회원 데이터 생성됨");

        logger.trace("DB에 회원 데이터를 저장");
        accountRepository.save(account);
    }

    // 유저 중복 확인 등의 unique 검사
    public void validateUniqueness(RegisterRequestDto request) {
        logger.debug("회원가입 유효성 검증 시작");

        // 유저 중복 확인
        logger.trace("유저 중복 여부 확인");
        if (accountRepository.existsByUsername(request.getUsername())) {
            logger.debug("이미 해당 유저가 존재");
            throw new RegisterFailedException(RegisterFailedType.USERNAME_NOT_UNIQUE);
        }
    }

    public JwtResponseDto generateJwtToken(String username) {
        logger.debug("JWT 토큰 발급 시작");
        logger.trace("액세스 토큰을 생성");
        Jwt accessToken = jwtService.generateAccessToken(username);
        logger.trace("리프레쉬 토큰을 생성");
        Jwt refreshToken = jwtService.generateRefreshToken(username);

        logger.trace("토큰을 담을 응답 객체 생성");
        JwtResponseDto jwtResponseDto = new JwtResponseDto();
        jwtResponseDto.setAccessToken(accessToken.getTokenValue());
        jwtResponseDto.setRefreshToken(refreshToken.getTokenValue());

        logger.debug("JWT 토큰 발급 성공");

        return jwtResponseDto;
    }

    public JwtResponseDto refreshTokens(String refreshToken) {
        logger.debug("리프레쉬 토큰 사용");
        String username = jwtService.consumeRefreshToken(refreshToken);
        logger.debug("리프레쉬 토큰 사용 완료");

        return generateJwtToken(username);
    }

    // 로그아웃: 현재 리프레쉬 토큰을 목록에서 제거
    public void logout(String refreshToken) {
        logger.debug("로그아웃 처리");
        jwtService.consumeRefreshToken(refreshToken);
        logger.debug("로그아웃 완료");
    }
}
