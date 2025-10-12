package com.notesapp.backend.exception;

public class EmailVerificationTokenExpiredException extends RuntimeException {
  public EmailVerificationTokenExpiredException() { super("Email verification token has expired"); }
  public EmailVerificationTokenExpiredException(String message) { super(message); }
  public EmailVerificationTokenExpiredException(String message, Throwable cause) { super(message, cause); }
}
