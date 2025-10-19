package com.notesapp.backend.exception;

public class UnauthorizedSessionAccessException extends RuntimeException {
    public UnauthorizedSessionAccessException() {super("Unauthorized: You cannot access this session");}
    public UnauthorizedSessionAccessException(String message) {super(message);}
    public UnauthorizedSessionAccessException(String message, Throwable cause) {super(message, cause);}
}
