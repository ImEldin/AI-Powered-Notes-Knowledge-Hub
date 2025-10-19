package com.notesapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {
    private String sessionId;
    private String device;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean current;
}
