package com.notesapp.backend.exception;

public class RefreshTokenMissingException extends RuntimeException {
    public RefreshTokenMissingException() { super("Refresh token missing");}
    public RefreshTokenMissingException(String message) { super(message);}
    public RefreshTokenMissingException(String message, Throwable cause) { super(message, cause); }
}
