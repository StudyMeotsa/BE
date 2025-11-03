package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.JwtResponseDto;
import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.exception.PasswordConfirmIncorrectException;
import com.example.growingstudy.auth.exception.UserAlreadyExistsException;
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
        logger.info("회원가입 시작");
        validate(request);

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setNickname(request.getNickname());

        accountRepository.save(account);
        logger.info("회원가입 성공");
    }

    // 유저 중복 확인, 패스워드 일치 확인
    public void validate(RegisterRequestDto request) {
        // 유저 중복 확인
        if (accountRepository.existsByUsername(request.getUsername())) {
            logger.error("이미 해당 유저가 존재");
            throw new UserAlreadyExistsException("이미 해당 username을 사용하는 유저가 존재합니다.");
        }

        // 패스워드 일치 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            logger.error("비밀번호와 비밀번호 확인이 불일치");
            throw new PasswordConfirmIncorrectException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }

    public JwtResponseDto generateJwtToken(String username) {
        logger.info("JWT 토큰 발급 시작");
        Jwt accessToken = jwtService.generateAccessToken(username);
        Jwt refreshToken = jwtService.generateRefreshToken(username);

        JwtResponseDto jwtResponseDto = new JwtResponseDto();
        jwtResponseDto.setAccessToken(accessToken.getTokenValue());
        jwtResponseDto.setRefreshToken(refreshToken.getTokenValue());

        logger.info("JWT 토큰 발급 완료");

        return jwtResponseDto;
    }

    public JwtResponseDto refreshTokens(String refreshToken) {
        logger.info("리프레쉬 토큰 사용");
        String username = jwtService.consumeRefreshToken(refreshToken);
        logger.info("리프레쉬 토큰 사용 완료");

        return generateJwtToken(username);
    }
}
