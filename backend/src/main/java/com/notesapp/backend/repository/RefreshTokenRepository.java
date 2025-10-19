package com.notesapp.backend.repository;

import com.notesapp.backend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    RefreshToken findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserId(Long userId);

    @Transactional
    @Modifying
    void deleteByTokenHash(String tokenHash);

    @Transactional
    @Modifying
    void deleteAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
