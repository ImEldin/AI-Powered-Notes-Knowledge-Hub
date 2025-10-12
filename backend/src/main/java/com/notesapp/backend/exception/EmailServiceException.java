package com.notesapp.backend.exception;

public class EmailServiceException extends RuntimeException {
    public EmailServiceException() {super("Email service error");}
    public EmailServiceException(String message) { super(message); }
    public EmailServiceException(String message, Throwable cause) { super(message, cause); }
}