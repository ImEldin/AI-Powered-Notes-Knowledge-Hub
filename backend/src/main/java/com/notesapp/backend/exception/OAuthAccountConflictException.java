package com.notesapp.backend.exception;

public class OAuthAccountConflictException extends RuntimeException {
    public OAuthAccountConflictException() {super("OAuth account conflict");}
    public OAuthAccountConflictException(String message) { super(message); }
    public OAuthAccountConflictException(String message, Throwable cause) { super(message, cause); }
}