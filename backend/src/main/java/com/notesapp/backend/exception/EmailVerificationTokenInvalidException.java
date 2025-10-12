package com.notesapp.backend.exception;

public class EmailVerificationTokenInvalidException extends RuntimeException {
    public EmailVerificationTokenInvalidException() { super("Invalid email verification token"); }
    public EmailVerificationTokenInvalidException(String message) { super(message); }
    public EmailVerificationTokenInvalidException(String message, Throwable cause) { super(message, cause); }
}
