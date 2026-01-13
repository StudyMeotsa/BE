package com.example.growingstudy.security.repository;

import com.example.growingstudy.security.entity.OnlyUidOfRefreshToken;
import com.example.growingstudy.security.entity.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RefreshTokenRepository 스프링 부트 통합 테스트
 */
@SpringBootTest
class RefreshTokenRepositorySpringIntegrationTests {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String testJid;
    private long testUid;

    @BeforeEach
    void setUp() {
        testJid = "test-jid-" + UUID.randomUUID();
        testUid = System.currentTimeMillis();
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteById(testJid);
    }

    @Test
    @DisplayName("리프레쉬 토큰 저장 테스트")
    void saveRefreshToken() {
        // given
        RefreshToken refreshToken = new RefreshToken(testJid, testUid);

        // when
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        // then
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getJid()).isEqualTo(testJid);
        assertThat(savedToken.getUid()).isEqualTo(testUid);
    }

    @Test
    @DisplayName("토큰 ID로 리프레쉬 토큰 조회 - 존재하는 경우")
    void findByIdWhenTokenExists() {
        // given
        RefreshToken refreshToken = new RefreshToken(testJid, testUid);
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> foundToken = refreshTokenRepository.findById(testJid);

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getJid()).isEqualTo(testJid);
        assertThat(foundToken.get().getUid()).isEqualTo(testUid);
    }

    @Test
    @DisplayName("토큰 ID로 리프레쉬 토큰 조회 - 존재하지 않는 경우")
    void findByIdWhenTokenNotExists() {
        // given
        String nonExistentJid = "non-existent-jid";

        // when
        Optional<RefreshToken> foundToken = refreshTokenRepository.findById(nonExistentJid);

        // then
        assertThat(foundToken).isEmpty();
    }

    @Test
    @DisplayName("토큰 ID로 유저 ID(uid) 조회 - 존재하는 경우")
    void findUidByJidWhenTokenExists() {
        // given
        RefreshToken refreshToken = new RefreshToken(testJid, testUid);
        refreshTokenRepository.save(refreshToken);

        // when
        OnlyUidOfRefreshToken result = refreshTokenRepository.findUidByJid(testJid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUid()).isEqualTo(testUid);
    }

    @Test
    @DisplayName("토큰 ID로 유저 ID(uid) 조회 - 존재하지 않는 경우")
    void findUidByJidWhenTokenNotExists() {
        // given
        String nonExistentJid = "non-existent-jid";

        // when
        OnlyUidOfRefreshToken result = refreshTokenRepository.findUidByJid(nonExistentJid);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("유저 ID로 모든 리프레쉬 토큰 조회 - 토큰이 존재하는 경우")
    void findAllByUidWhenTokensExist() {
        // given
        String jid1 = "test-jid-1-" + UUID.randomUUID();
        String jid2 = "test-jid-2-" + UUID.randomUUID();

        RefreshToken token1 = new RefreshToken(jid1, testUid);
        RefreshToken token2 = new RefreshToken(jid2, testUid);

        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);

        try {
            // when
            List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(testUid);

            // then
            assertThat(tokens).hasSize(2);
            assertThat(tokens).extracting(RefreshToken::getUid)
                    .containsOnly(testUid);
            assertThat(tokens).extracting(RefreshToken::getJid)
                    .containsExactlyInAnyOrder(jid1, jid2);
        } finally {
            refreshTokenRepository.deleteById(jid1);
            refreshTokenRepository.deleteById(jid2);
        }
    }

    @Test
    @DisplayName("유저 ID로 모든 리프레쉬 토큰 조회 - 토큰이 존재하지 않는 경우")
    void findAllByUidWhenNoTokensExist() {
        // given
        long nonExistentUid = 999999999L;

        // when
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUid(nonExistentUid);

        // then
        assertThat(tokens).isEmpty();
    }

    @Test
    @DisplayName("리프레쉬 토큰 삭제 테스트")
    void deleteRefreshToken() {
        // given
        RefreshToken refreshToken = new RefreshToken(testJid, testUid);
        refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenRepository.deleteById(testJid);

        // then
        Optional<RefreshToken> deletedToken = refreshTokenRepository.findById(testJid);
        assertThat(deletedToken).isEmpty();
    }

    @Test
    @DisplayName("리스트에 포함된 토큰들만 삭제 테스트")
    void deleteAllByListDeletesOnlySpecifiedTokens() {
        // given
        String jid1 = "delete-all-jid-1-" + UUID.randomUUID();
        String jid2 = "delete-all-jid-2-" + UUID.randomUUID();
        String jidToRemain = "should-remain-jid-" + UUID.randomUUID();

        RefreshToken token1 = new RefreshToken(jid1, 1L);
        RefreshToken token2 = new RefreshToken(jid2, 2L);
        RefreshToken tokenNotToDelete = new RefreshToken(jidToRemain, 3L);

        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);
        refreshTokenRepository.save(tokenNotToDelete);

        List<RefreshToken> tokensToDelete = List.of(token1, token2);

        try {
            // when
            refreshTokenRepository.deleteAll(tokensToDelete);

            // then
            assertThat(refreshTokenRepository.findById(jid1)).isEmpty();
            assertThat(refreshTokenRepository.findById(jid2)).isEmpty();
            assertThat(refreshTokenRepository.findById(jidToRemain)).isPresent();
        } finally {
            refreshTokenRepository.deleteById(jidToRemain);
        }
    }
}
