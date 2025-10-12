package com.notesapp.backend.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() { super("Invalid email or password"); }
    public InvalidCredentialsException(String message) { super(message); }
    public InvalidCredentialsException(String message, Throwable cause) { super(message, cause); }
}
