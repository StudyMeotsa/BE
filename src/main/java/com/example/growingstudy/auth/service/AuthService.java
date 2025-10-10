package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.domain.Account;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.exception.PasswordConfirmIncorrectException;
import com.example.growingstudy.auth.exception.UserAlreadyExistsException;
import com.example.growingstudy.auth.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
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
}
