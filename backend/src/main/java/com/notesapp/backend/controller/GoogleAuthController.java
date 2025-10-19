package com.notesapp.backend.controller;

import com.notesapp.backend.dto.AuthResponseDTO;
import com.notesapp.backend.dto.GoogleLoginRequestDTO;
import com.notesapp.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final AuthService authService;

    @PostMapping("/google-login")
    public ResponseEntity<AuthResponseDTO> googleLogin(
            @Valid @RequestBody GoogleLoginRequestDTO request,
            HttpServletResponse response,
            HttpServletRequest httpRequest) {
        AuthResponseDTO authResponse = authService.googleLogin(request.getIdToken(), response, httpRequest);
        return ResponseEntity.ok(authResponse);
    }
}
