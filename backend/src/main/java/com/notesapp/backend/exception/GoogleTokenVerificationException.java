package com.notesapp.backend.exception;

public class GoogleTokenVerificationException extends RuntimeException {
    public GoogleTokenVerificationException() {super("Google token verification error");}
    public GoogleTokenVerificationException(String message) { super(message); }
    public GoogleTokenVerificationException(String message, Throwable cause) { super(message, cause); }
}
