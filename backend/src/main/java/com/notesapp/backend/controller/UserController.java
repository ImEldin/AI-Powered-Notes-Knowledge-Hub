package com.notesapp.backend.controller;

import com.notesapp.backend.dto.ApiResponseDTO;
import com.notesapp.backend.dto.SessionDTO;
import com.notesapp.backend.service.RefreshTokenService;
import com.notesapp.backend.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDTO>> getActiveSessions(
            Authentication authentication,
            HttpServletRequest request) {

        Long userId = (Long) authentication.getPrincipal();

        String currentRefreshToken = cookieUtil.getCookieValue(request, "refresh_token");
        String currentTokenHash = currentRefreshToken != null
                ? refreshTokenService.hashToken(currentRefreshToken)
                : null;

        List<SessionDTO> sessions = refreshTokenService.getActiveSessions(userId, currentTokenHash);

        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponseDTO> revokeSession(
            @PathVariable String sessionId,
            Authentication authentication,
            HttpServletRequest request) {

        String jwt = cookieUtil.getJwtFromCookie(request);

        Long userId = (Long) authentication.getPrincipal();

        UUID sessionUuid = UUID.fromString(sessionId);

        refreshTokenService.revokeSessionById(sessionUuid, userId);

        return ResponseEntity.ok(new ApiResponseDTO("Session revoked successfully"));
    }
}