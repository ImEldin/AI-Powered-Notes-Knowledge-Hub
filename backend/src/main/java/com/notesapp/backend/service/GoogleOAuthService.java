package com.notesapp.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.notesapp.backend.exception.GoogleTokenVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {

    @Value("${app.google.client_id}")
    private String clientId;

    public GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new GoogleTokenVerificationException("Invalid ID token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            if (!payload.getAudience().equals(clientId)) {
                throw new GoogleTokenVerificationException("Audience mismatch");
            }

            long expirationTimeMillis = payload.getExpirationTimeSeconds() * 1000L;
            if (new Date().after(new Date(expirationTimeMillis))) {
                throw new GoogleTokenVerificationException("Token expired");
            }

            return payload;
        } catch (GoogleTokenVerificationException e) {
            log.warn("Google token verification failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Google token verification error", e);
            throw new GoogleTokenVerificationException("Token verification failed", e);
        }
    }
}
