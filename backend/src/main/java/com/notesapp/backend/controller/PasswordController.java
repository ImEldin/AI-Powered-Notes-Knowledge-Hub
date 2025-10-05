package com.notesapp.backend.controller;

import com.notesapp.backend.dto.ApiResponseDTO;
import com.notesapp.backend.dto.ForgotPasswordDTO;
import com.notesapp.backend.dto.ResetPasswordDTO;
import com.notesapp.backend.service.AuthService;
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
public class PasswordController {

    private final AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordDTO request) {

        ApiResponseDTO response =  authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO> resetPassword(
            @Valid @RequestBody ResetPasswordDTO request) {

        ApiResponseDTO response = authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(response);
    }
}
