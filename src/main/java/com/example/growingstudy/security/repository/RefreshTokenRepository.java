package com.example.growingstudy.security.repository;

import com.example.growingstudy.security.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

/**
 * 리프레쉬 토큰 DB에 액세스하는 레포지토리
 */
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
