package com.notesapp.backend.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() { super("Session not found");}
    public SessionNotFoundException(String sessionId) {super("Session not found: " + sessionId);}
    public SessionNotFoundException(String sessionId, Throwable cause) { super("Session not found: " + sessionId, cause); }
}
