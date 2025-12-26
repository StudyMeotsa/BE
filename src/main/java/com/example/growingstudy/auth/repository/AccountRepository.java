package com.example.growingstudy.auth.repository;

import com.example.growingstudy.auth.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 회원 DB에 액세스하는 레포지토리
 */
public interface AccountRepository extends CrudRepository<Account, Long> {

    /**
     * 해당 username을 가진 회원을 조회
     * @param username 조회 대상 유저의 username
     * @return Optional로 감싸진 유저 조회 결과
     */
    @Query("select a from Account a where a.username = :username")
    Optional<Account> findByUsername(@Param("username") String username);

    /**
     * 해당 username을 가진 회원의 존재 여부 반환
     * @param username 조회 대상 유저의 username
     * @return 회원 존재 여부
     */
    boolean existsByUsername(@Param("username") String username);
}
