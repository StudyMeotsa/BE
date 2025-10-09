package com.example.growingstudy.repository;

import com.example.growingstudy.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class AccountRepositorySpringIntegrationTests {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Account 저장 테스트")
    public void saveAccount() {
        Account account = new Account();
        account.setUsername("username");
        account.setPassword("password");
        account.setNickname("nickname");

        accountRepository.save(account);
    }

    @Test
    @DisplayName("Account 저장 및 username으로 Account 얻어오기")
    public void saveAndFindByUsername() {
        Account account = new Account();
        account.setUsername("username");
        account.setPassword("password");
        account.setNickname("nickname");

        accountRepository.save(account);

        Account accountActual = accountRepository.findByUsername("username").get();

        // 저장한 Account와 정보 같은지 확인
        assertEquals(account.getUsername(), accountActual.getUsername(), "예상 username과 실제 username이 다름");
        assertEquals(account.getPassword(), accountActual.getPassword(), "예상 password와 실제 password가 다름");
        assertEquals(account.getNickname(), accountActual.getNickname(), "예상 nickname과 실제 nickname이 다름");
    }

    @Test
    @DisplayName("Account 저장 및 저장한 유저 존재하는지 확인")
    public void saveAndExistsByUsername() {
        Account account = new Account();
        account.setUsername("username");
        account.setPassword("password");
        account.setNickname("nickname");

        accountRepository.save(account);

        assertTrue(accountRepository.existsByUsername("username"), "예상과 달리 유저가 존재하지 않음");
    }
}
