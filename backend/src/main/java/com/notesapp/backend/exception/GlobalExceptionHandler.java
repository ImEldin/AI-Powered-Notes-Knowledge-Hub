package com.notesapp.backend.exception;

import com.notesapp.backend.dto.ApiResponseDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        FieldError fe = ex.getBindingResult().getFieldError();
        String message = fe != null ? fe.getField() + ": " + fe.getDefaultMessage() : "Validation failed";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(message));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("Malformed request body: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO("Malformed request body"));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("No handler found for {}", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO("Endpoint not found"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO("Validation error"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        ex.getMostSpecificCause();
        log.error("Database constraint violated: {}", ex.getMostSpecificCause().getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO("Database constraint violation"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO("Authentication failed"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO("Access denied"));
    }

    //-------------------------------------------------------------------------------------------

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Invalid credentials attempt");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponseDTO> handleEmailNotVerified(EmailNotVerifiedException ex) {
        log.info("Email not verified: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponseDTO> handleAccountLocked(AccountLockedException ex) {
        log.info("Account locked: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.LOCKED).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponseDTO> handleEmailAlreadyVerified(EmailAlreadyVerifiedException ex) {
        log.info("Email already verified: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(EmailVerificationTokenInvalidException.class)
    public ResponseEntity<ApiResponseDTO> handleEmailVerificationInvalid(EmailVerificationTokenInvalidException ex) {
        log.info("Invalid email verification token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(EmailVerificationTokenExpiredException.class)
    public ResponseEntity<ApiResponseDTO> handleEmailVerificationExpired(EmailVerificationTokenExpiredException ex) {
        log.info("Expired email verification token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(EmailServiceException.class)
    public ResponseEntity<ApiResponseDTO> handleEmailServiceError(EmailServiceException ex) {
        log.error("Email service error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponseDTO("Failed to send email"));
    }

    @ExceptionHandler(GoogleTokenVerificationException.class)
    public ResponseEntity<ApiResponseDTO> handleGoogleTokenVerification(GoogleTokenVerificationException ex) {
        log.warn("Google token verification failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO("Invalid Google token"));
    }

    @ExceptionHandler(OAuthAccountConflictException.class)
    public ResponseEntity<ApiResponseDTO> handleOAuthConflict(OAuthAccountConflictException ex) {
        log.warn("OAuth account conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenInvalidException.class)
    public ResponseEntity<ApiResponseDTO> handleInvalidPasswordResetToken(PasswordResetTokenInvalidException ex) {
        log.info("Invalid password reset token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    public ResponseEntity<ApiResponseDTO> handleExpiredPasswordResetToken(PasswordResetTokenExpiredException ex) {
        log.info("Expired password reset token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiResponseDTO> handleTooManyRequests(TooManyRequestsException ex) {
        log.warn("Rate limit hit: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<ApiResponseDTO> handleUserDeactivated(UserDeactivatedException ex) {
        log.info("User deactivated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        log.info("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(RefreshTokenMissingException.class)
    public ResponseEntity<ApiResponseDTO> handleRefreshTokenMissing(RefreshTokenMissingException ex) {
        log.warn("Refresh token missing");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ApiResponseDTO> handleRefreshTokenInvalid(RefreshTokenInvalidException ex) {
        log.warn("Refresh token invalid or expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleSessionNotFound(SessionNotFoundException ex) {
        log.warn("Session not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedSessionAccessException.class)
    public ResponseEntity<ApiResponseDTO> handleUnauthorizedSessionAccess(UnauthorizedSessionAccessException ex) {
        log.warn("Unauthorized session access attempt");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ApiResponseDTO> handleInvalidJwtToken(InvalidJwtTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDTO(ex.getMessage()));
    }

    //-------------------------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleAll(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("An unexpected error occurred"));
    }

    //-------------------------------------------------------------------------------------------

}


