package com.notesapp.backend.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() { super("Account is temporarily locked. Try again later."); }
    public AccountLockedException(String message) { super(message); }
    public AccountLockedException(String message, Throwable cause) { super(message, cause); }
}
