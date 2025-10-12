package com.notesapp.backend.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {
    public EmailAlreadyVerifiedException() { super("Email is already verified"); }
    public EmailAlreadyVerifiedException(String message) { super(message); }
    public EmailAlreadyVerifiedException(String message, Throwable cause) { super(message, cause); }
}