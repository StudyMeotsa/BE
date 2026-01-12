package com.example.growingstudy.security.repository;

import com.example.growingstudy.security.entity.OnlyUidOfRefreshToken;
import com.example.growingstudy.security.entity.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RefreshTokenRepository 단위 테스트
 */
@DataRedisTest
class RefreshTokenRepositoryUnitTests {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("리프레쉬 토큰 저장 테스트")
    void testSave() {
        // given
        RefreshToken refreshToken = new RefreshToken("test-jid-12345", 1L);

        // when
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        // then
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getJid()).isEqualTo("test-jid-12345");
        assertThat(savedToken.getUid()).isEqualTo(1L);
    }

    @Test
    @DisplayName("저장 후 조회 테스트")
    void testSaveAndFindById() {
        // given
        RefreshToken refreshToken = new RefreshToken("test-jid-67890", 2L);
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> foundToken = refreshTokenRepository.findById("test-jid-67890");

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getJid()).isEqualTo("test-jid-67890");
        assertThat(foundToken.get().getUid()).isEqualTo(2L);
    }

    @Test
    @DisplayName("jid로 uid 조회 테스트 - 존재하는 경우")
    void testFindUidByJidWhenExists() {
        // given
        RefreshToken refreshToken = new RefreshToken("uid-lookup-jid", 100L);
        refreshTokenRepository.save(refreshToken);

        // when
        OnlyUidOfRefreshToken result = refreshTokenRepository.findUidByJid("uid-lookup-jid");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUid()).isEqualTo(100L);
    }

    @Test
    @DisplayName("jid로 uid 조회 테스트 - 존재하지 않는 경우")
    void testFindUidByJidWhenNotExists() {
        // given
        String nonExistentJid = "non-existent-jid";

        // when
        OnlyUidOfRefreshToken result = refreshTokenRepository.findUidByJid(nonExistentJid);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("uid로 모든 토큰 조회 테스트 - 토큰이 존재하는 경우")
    void testFindAllByUidWhenExists() {
        // given
        long targetUid = 50L;
        RefreshToken token1 = new RefreshToken("token-1-for-uid-50", targetUid);
        RefreshToken token2 = new RefreshToken("token-2-for-uid-50", targetUid);
        RefreshToken token3 = new RefreshToken("token-for-uid-99", 99L);
        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);
        refreshTokenRepository.save(token3);

        // when
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(targetUid);

        // then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(RefreshToken::getUid)
                .containsOnly(targetUid);
        assertThat(tokens).extracting(RefreshToken::getJid)
                .containsExactlyInAnyOrder("token-1-for-uid-50", "token-2-for-uid-50");
    }

    @Test
    @DisplayName("uid로 모든 토큰 조회 테스트 - 토큰이 존재하지 않는 경우")
    void testFindAllByUidWhenNotExists() {
        // given
        long nonExistentUid = 999L;

        // when
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(nonExistentUid);

        // then
        assertThat(tokens).isEmpty();
    }

    @Test
    @DisplayName("토큰 삭제 테스트")
    void testDelete() {
        // given
        RefreshToken refreshToken = new RefreshToken("delete-test-jid", 10L);
        refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenRepository.deleteById("delete-test-jid");

        // then
        Optional<RefreshToken> deletedToken = refreshTokenRepository.findById("delete-test-jid");
        assertThat(deletedToken).isEmpty();
    }

    @Test
    @DisplayName("토큰 존재 여부 확인 테스트")
    void testExistsById() {
        // given
        RefreshToken refreshToken = new RefreshToken("exists-test-jid", 20L);
        refreshTokenRepository.save(refreshToken);

        // when & then
        assertThat(refreshTokenRepository.existsById("exists-test-jid")).isTrue();
        assertThat(refreshTokenRepository.existsById("non-existent-jid")).isFalse();
    }

    @Test
    @DisplayName("리스트에 포함된 토큰들만 삭제 테스트")
    void testDeleteAllByList() {
        // given
        RefreshToken token1 = new RefreshToken("delete-all-jid-1", 1L);
        RefreshToken token2 = new RefreshToken("delete-all-jid-2", 2L);
        RefreshToken tokenNotToDelete = new RefreshToken("should-remain-jid", 3L);
        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);
        refreshTokenRepository.save(tokenNotToDelete);

        List<RefreshToken> tokensToDelete = List.of(token1, token2);

        // when
        refreshTokenRepository.deleteAll(tokensToDelete);

        // then
        assertThat(refreshTokenRepository.findById("delete-all-jid-1")).isEmpty();
        assertThat(refreshTokenRepository.findById("delete-all-jid-2")).isEmpty();
        assertThat(refreshTokenRepository.findById("should-remain-jid")).isPresent();
    }
}
