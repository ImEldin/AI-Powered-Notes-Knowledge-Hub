package com.notesapp.backend.exception;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException() { super("Invalid or expired JWT token"); }
    public InvalidJwtTokenException(String message) {
        super(message);
    }
    public InvalidJwtTokenException(String message, Throwable cause) {}
}
