package com.example.growingstudy.auth.repository;

import com.example.growingstudy.auth.domain.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    @Query("select a from Account a where a.username = :username")
    Optional<Account> findByUsername(@Param("username") String username);

    boolean existsByUsername(@Param("username") String username);
}
