package com.notesapp.backend.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() { super("Email address not verified"); }
    public EmailNotVerifiedException(String message) { super(message); }
    public EmailNotVerifiedException(String message, Throwable cause) { super(message, cause); }
}
