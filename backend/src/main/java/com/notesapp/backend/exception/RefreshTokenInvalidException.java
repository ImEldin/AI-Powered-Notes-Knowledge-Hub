package com.notesapp.backend.exception;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException() {super("Invalid or expired refresh token");}
    public RefreshTokenInvalidException(String message) {super(message);}
    public RefreshTokenInvalidException(String message, Throwable cause) {super(message, cause);}

}
