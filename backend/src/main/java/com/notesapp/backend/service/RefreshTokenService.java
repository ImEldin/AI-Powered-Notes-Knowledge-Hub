package com.notesapp.backend.service;

import com.notesapp.backend.dto.SessionDTO;
import com.notesapp.backend.exception.SessionNotFoundException;
import com.notesapp.backend.exception.UnauthorizedSessionAccessException;
import com.notesapp.backend.model.RefreshToken;
import com.notesapp.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.auth.refresh-token-expiration-days:7}")
    private long refreshTokenDays;

    @Value("${app.auth.remember-refresh-token-expiration-days:30}")
    private long rememberRefreshTokenDays;

    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public String createToken(Long userId, boolean rememberMe, String device) {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        String tokenHash = hashToken(rawToken);

        Instant now = Instant.now();
        Instant expires = now.plus(rememberMe ? rememberRefreshTokenDays : refreshTokenDays, ChronoUnit.DAYS);

        RefreshToken rt = new RefreshToken(
                UUID.randomUUID(),
                tokenHash,
                userId,
                device,
                now,
                expires
        );
        repository.save(rt);

        return rawToken;
    }

    public RefreshToken verify(String rawToken) {
        String hash = hashToken(rawToken);
        RefreshToken rt = repository.findByTokenHash(hash);

        if (rt == null || rt.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        return rt;
    }

    @Transactional
    public String rotate(RefreshToken old, boolean rememberMe, String device) {
        repository.deleteByTokenHash(old.getTokenHash());
        return createToken(old.getUserId(), rememberMe, device);
    }

    @Transactional
    public void revoke(String rawToken) {
        String hash = hashToken(rawToken);
        repository.deleteByTokenHash(hash);
    }

    @Transactional
    public void revokeAllForUser(Long userId) {
        repository.deleteAllByUserId(userId);
    }

    @Transactional
    public void deleteExpiredTokens() {
        repository.deleteExpiredTokens();
    }

    public List<SessionDTO> getActiveSessions(Long userId, String currentTokenHash) {
        List<RefreshToken> tokens = repository.findAllByUserId(userId);

        return tokens.stream()
                .map(token -> new SessionDTO(
                        token.getId().toString(),
                        token.getDevice(),
                        token.getCreatedAt(),
                        token.getExpiresAt(),
                        token.getTokenHash().equals(currentTokenHash)
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokeSessionById(UUID sessionId, Long userId) {
        RefreshToken token = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId.toString()));

        if (!token.getUserId().equals(userId)) {
            throw new UnauthorizedSessionAccessException();
        }

        log.info("Revoking session {} for userId={}", sessionId, userId);
        repository.deleteById(sessionId);
    }
}
