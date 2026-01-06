package com.example.growingstudy.security.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import com.example.growingstudy.security.entity.UserDetailsWithId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AccountRepositoryUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(AccountRepositoryUserDetailsService.class);
    private final AccountRepository accountRepository;

    public AccountRepositoryUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // ERD 변경에 따라 parameter는 email로 변경하였으나 UserDetailsService 스펙으로 메소드 이름은 그대로 유지
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("DB에서 유저 정보 획득 시작");
        Account account =
                accountRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException(email));

        logger.info("UserDetails 빌드");
        UserDetails user =
                UserDetailsWithId.builder()
                        .username(email)
                        .password(account.getPassword())
                        .userId(account.getId())
                        .build();

        account.setPassword(null);

        return user;
    }
}
