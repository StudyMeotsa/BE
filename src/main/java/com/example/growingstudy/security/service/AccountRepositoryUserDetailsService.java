package com.example.growingstudy.security.service;

import com.example.growingstudy.auth.entity.Account;
import com.example.growingstudy.auth.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AccountRepositoryUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(AccountRepositoryUserDetailsService.class);
    private final AccountRepository accountRepository;

    public AccountRepositoryUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("DB에서 유저 정보 획득 시작");
        Account account =
                accountRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username));

        logger.info("UserDetails 빌드");
        UserDetails user
                = User.withUsername(username).password(account.getPassword()).build();

        account.setPassword(null);

        return user;
    }
}
