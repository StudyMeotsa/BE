package com.example.growingstudy.security.repository;

import com.example.growingstudy.security.entity.OnlyUidOfRefreshToken;
import com.example.growingstudy.security.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

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

    /**
     * 해당 유저 id를 subject로 하는 리프레쉬 토큰 리스트 반환
     * @param uid 유저 id
     * @return 리프레쉬 토큰 리스트
     */
    List<RefreshToken> findAllByUid(long uid);
}
