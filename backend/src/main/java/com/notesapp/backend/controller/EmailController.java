package com.notesapp.backend.controller;

import com.notesapp.backend.dto.ApiResponseDTO;
import com.notesapp.backend.dto.EmailVerificationRequestDTO;
import com.notesapp.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailController {

    private final AuthService authService;

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO> verifyEmail(@RequestParam("token") String token) {
        ApiResponseDTO apiResponse = authService.verifyEmail(token);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponseDTO> resendVerification(
            @Valid @RequestBody EmailVerificationRequestDTO request) {
        ApiResponseDTO apiResponse = authService.resendVerification(request.getEmail());
        return ResponseEntity.ok(apiResponse);
    }
}