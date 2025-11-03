package com.notesapp.backend.exception;

public class InvalidJwtTokenException extends RuntimeException {
  public InvalidJwtTokenException(String message) {
    super(message);
  }
}
