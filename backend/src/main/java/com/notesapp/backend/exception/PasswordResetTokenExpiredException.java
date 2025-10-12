package com.notesapp.backend.exception;

public class PasswordResetTokenExpiredException extends RuntimeException {
    public PasswordResetTokenExpiredException() { super("Reset token has expired"); }
    public PasswordResetTokenExpiredException(String message) { super(message); }
    public PasswordResetTokenExpiredException(String message, Throwable cause) { super(message, cause); }
}
