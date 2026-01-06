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
     * 해당 이메일을 가진 회원을 조회
     * @param email 조회 대상 유저의 이메일
     * @return Optional로 감싸진 유저 조회 결과
     */
    @Query("select a from Account a where a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    /**
     * 해당 이메일을 가진 회원의 존재 여부 반환
     * @param email 조회 대상 유저의 이메일
     * @return 회원 존재 여부
     */
    boolean existsByEmail(@Param("email") String email);
}
