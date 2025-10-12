package com.notesapp.backend.exception;

public class UserDeactivatedException extends RuntimeException {
    public UserDeactivatedException() { super("User account is deactivated"); }
    public UserDeactivatedException(String message) { super(message); }
    public UserDeactivatedException(String message, Throwable cause) { super(message, cause); }
}
