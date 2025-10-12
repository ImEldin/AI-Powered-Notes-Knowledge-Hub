package com.notesapp.backend.exception;

public class PasswordResetTokenInvalidException extends RuntimeException {
  public PasswordResetTokenInvalidException() { super("Invalid or expired reset token"); }
  public PasswordResetTokenInvalidException(String message) { super(message); }
  public PasswordResetTokenInvalidException(String message, Throwable cause) { super(message, cause); }
}