package com.notesapp.backend.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {super("User already exists");}
    public UserAlreadyExistsException(String message) {super(message);}
    public UserAlreadyExistsException(String message, Throwable cause) { super(message, cause); }
}
