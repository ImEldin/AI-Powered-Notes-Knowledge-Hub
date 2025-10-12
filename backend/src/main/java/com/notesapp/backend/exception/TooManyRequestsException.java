package com.notesapp.backend.exception;

public class TooManyRequestsException extends RuntimeException {
  public TooManyRequestsException() { super("Too many requests"); }
  public TooManyRequestsException(String message) { super(message); }
  public TooManyRequestsException(String message, Throwable cause) { super(message, cause); }
}
