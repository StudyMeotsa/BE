package com.example.growingstudy.security.repository;

import com.example.growingstudy.security.entity.OnlyUidOfRefreshToken;
import com.example.growingstudy.security.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

/**
 * 리프레쉬 토큰 DB에 액세스하는 레포지토리
 */
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    /**
     * 토큰 id가 jid에 해당하는 토큰의 subject(uid) 반환
     * @param jid 토큰 id
     * @return 토큰의 subject(uid)
     */
    OnlyUidOfRefreshToken findUidByJid(String jid);
}
