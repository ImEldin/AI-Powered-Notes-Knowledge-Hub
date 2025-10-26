package com.notesapp.backend.controller;

import com.notesapp.backend.dto.ApiResponseDTO;
import com.notesapp.backend.dto.AuthResponseDTO;
import com.notesapp.backend.dto.LoginRequestDTO;
import com.notesapp.backend.dto.RegisterRequestDTO;
import com.notesapp.backend.exception.RefreshTokenInvalidException;
import com.notesapp.backend.exception.RefreshTokenMissingException;
import com.notesapp.backend.exception.UserNotFoundException;
import com.notesapp.backend.model.User;
import com.notesapp.backend.repository.UserRepository;
import com.notesapp.backend.security.JwtTokenProvider;
import com.notesapp.backend.service.AuthService;
import com.notesapp.backend.service.RefreshTokenService;
import com.notesapp.backend.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.notesapp.backend.model.RefreshToken;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request,
            HttpServletResponse response,
            HttpServletRequest httpRequest) {

        AuthResponseDTO authResponse = authService.register(request, response, httpRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response,
            HttpServletRequest httpRequest) {

        AuthResponseDTO authResponse = authService.login(request, response, httpRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO> logout(HttpServletResponse response, HttpServletRequest httpRequest) {
        authService.logout(response, httpRequest);
        ApiResponseDTO apiResponse = new ApiResponseDTO("Logout successful");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponseDTO> logoutAll(
            Authentication authentication,
            HttpServletResponse response) {

        Long userId = (Long) authentication.getPrincipal();

        refreshTokenService.revokeAllForUser(userId);

        cookieUtil.clearJwtCookie(response);
        cookieUtil.clearCookie(response, "refresh_token");

        return ResponseEntity.ok(new ApiResponseDTO("Logged out from all devices successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String rawRefreshToken = cookieUtil.getCookieValue(request, "refresh_token");
        if (rawRefreshToken == null) {
            throw new RefreshTokenMissingException();
        }

        RefreshToken refreshToken = refreshTokenService.verify(rawRefreshToken);
        if (refreshToken == null) {
            throw new RefreshTokenInvalidException();
        }

        User user = userRepository.findById(refreshToken.getUserId()).orElse(null);
        if (user == null) {
            refreshTokenService.revoke(rawRefreshToken);
            throw new UserNotFoundException();
        }

        boolean rememberMe = refreshToken.getExpiresAt().isAfter(
                java.time.Instant.now().plus(8, java.time.temporal.ChronoUnit.DAYS)
        );

        String newRawRefreshToken = refreshTokenService.rotate(
                refreshToken,
                rememberMe,
                request.getHeader("User-Agent")
        );
        int maxAge = rememberMe ? 30 * 24 * 3600 : 7 * 24 * 3600;
        cookieUtil.addCookie(response, "refresh_token", newRawRefreshToken, maxAge);

        String jwt = jwtTokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole());
        cookieUtil.addJwtCookie(response, jwt);

        return ResponseEntity.ok(new AuthResponseDTO(
                "Token refreshed",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEmailVerified()
        ));
    }
}
