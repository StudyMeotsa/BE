package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.MyPageResponseDto;
import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.dto.RegisterRequestDto;
import com.example.growingstudy.auth.enums.RegisterFailedType;
import com.example.growingstudy.auth.enums.SexEnum;
import com.example.growingstudy.auth.exception.RegisterFailedException;
import com.example.growingstudy.auth.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입과 같은 회원 관련 메소드를 제공하는 서비스 클래스
 */
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

    /**
     * 회원가입 요청 데이터를 받아 DB 레벨에서의 유효성 검증 후 계정을 생성 및 DB에 저장
     * @param request 회원가입 요청 DTO
     */
    @Transactional
    public void register(RegisterRequestDto request) {
        logger.debug("회원가입 서비스 로직 시작");

        logger.trace("회원가입 유효성을 검증");
        validateUniqueness(request);
        logger.trace("유효성 검증 성공");
        logger.trace("새 회원 데이터를 생성");
        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .sex(SexEnum.valueOf(request.getSex()))
                .build();

        logger.trace("새 회원 데이터 생성됨");

        logger.trace("DB에 회원 데이터를 저장");
        accountRepository.save(account);
    }

    /**
     * 유저 중복 여부 확인과 같은 DB 레벨에서의 유효성 검증
     * @param request 회원가입 요청 DTO
     */
    public void validateUniqueness(RegisterRequestDto request) {
        logger.debug("회원가입 유효성 검증 시작");

        // 유저 중복 확인
        logger.trace("유저 중복 여부 확인");
        if (accountRepository.existsByEmail(request.getEmail())) {
            logger.debug("이미 해당 유저가 존재");
            throw new RegisterFailedException(RegisterFailedType.USERNAME_NOT_UNIQUE);
        }
    }

    /**
     * id에 해당하는 유저의 정보를 찾아 이름, 성별, 이메일 정보가 담긴 객체를 반환
     * @param userId 정보를 찾을 유저의 id (Account.id)
     * @return 이름, 성별, 이메일 정보가 담긴 객체
     */
    public MyPageResponseDto retrieveUserInfo(long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow();

        MyPageResponseDto response = MyPageResponseDto.builder()
                .name(account.getName())
                .sex(String.valueOf(account.getSex()))
                .email(account.getEmail())
                .build();

        return response;
    }
}
