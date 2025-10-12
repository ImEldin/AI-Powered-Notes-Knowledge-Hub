package com.notesapp.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.notesapp.backend.dto.ApiResponseDTO;
import com.notesapp.backend.dto.AuthResponseDTO;
import com.notesapp.backend.dto.LoginRequestDTO;
import com.notesapp.backend.dto.RegisterRequestDTO;
import com.notesapp.backend.exception.*;
import com.notesapp.backend.model.AuthProvider;
import com.notesapp.backend.model.User;
import com.notesapp.backend.repository.UserRepository;
import com.notesapp.backend.security.JwtTokenProvider;
import com.notesapp.backend.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final EmailService emailService;
    private final GoogleOAuthService googleOAuthService;

    public AuthResponseDTO register(RegisterRequestDTO request, HttpServletResponse response){

        String displayName = request.getFirstName() + " " + request.getLastName();

        if(userRepository.findByEmail(request.getEmail()) != null){
            throw new UserAlreadyExistsException();
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                displayName
        );

        user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setEmailVerificationExpiresAt(LocalDateTime.now().plusHours(24));

        User savedUser = userRepository.save(user);

        String jwt = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());

        cookieUtil.addJwtCookie(response, jwt);

        emailService.sendEmailVerification(
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getEmailVerificationToken()
        );

        return new AuthResponseDTO(
                "Registration successful! Please check your email to verify your account.",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.isEmailVerified()
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request, HttpServletResponse response){

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new InvalidCredentialsException();
        }

        if(user.getProvider() != AuthProvider.LOCAL){
            throw new OAuthAccountConflictException(
                    "This account is linked via " + user.getProvider() + " and cannot use password login."
            );
        }

        if (!user.isActive()) {
            throw new UserDeactivatedException();
        }

        if (user.isLocked() && user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= 5) {
                user.setLocked(true);
                user.setLockedUntil(LocalDateTime.now().plusHours(1));
            }

            userRepository.save(user);
            throw new InvalidCredentialsException();
        }

        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLocked(false);
            user.setLockedUntil(null);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String jwt = jwtTokenProvider.generateToken(user.getEmail(), user.getId());

        cookieUtil.addJwtCookie(response, jwt);

        return new AuthResponseDTO(
                "Login successful",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEmailVerified()
        );

    }

    public AuthResponseDTO googleLogin(String idToken, HttpServletResponse response) {
        GoogleIdToken.Payload payload = googleOAuthService.verifyGoogleToken(idToken);

        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String googleUserId = payload.getSubject();
        String avatarUrl = (String) payload.get("picture");
        String displayName = firstName + " " + lastName;

        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmailVerified(true);
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(googleUserId);
            user.setAvatarUrl(avatarUrl);
            user.setDisplayName(displayName);
            user.setPassword(null);
        }

        user.setLastLoginAt(LocalDateTime.now());
        user = userRepository.save(user);

        String jwt = jwtTokenProvider.generateToken(user.getEmail(), user.getId());

        cookieUtil.addJwtCookie(response, jwt);

        return new AuthResponseDTO(
                "Google login successful!",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEmailVerified()
        );
    }

    public void logout(HttpServletResponse response){
        cookieUtil.clearJwtCookie(response);
    }

    public ApiResponseDTO verifyEmail(String token){
        User user = userRepository.findByEmailVerificationToken(token);

        if(user == null){
            throw new EmailVerificationTokenInvalidException();
        }

        if(user.getEmailVerificationExpiresAt().isBefore(LocalDateTime.now())){
            throw new EmailVerificationTokenExpiredException();
        }

        if(user.isEmailVerified()){
            throw new EmailAlreadyVerifiedException();
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiresAt(null);
        userRepository.save(user);

        return new ApiResponseDTO(
                "Email verified successfully!"
        );
    }

    public ApiResponseDTO resendVerification(String email){
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException();
        }

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException();
        }

        user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setEmailVerificationExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendEmailVerification(
                user.getEmail(),
                user.getFirstName(),
                user.getEmailVerificationToken()
        );

        return new ApiResponseDTO(
                "Verification email sent successfully!"
        );
    }

    public ApiResponseDTO forgotPassword(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ApiResponseDTO(
                    "If an account with that email exists, we've sent a password reset link."
            );
        }

        user.setPasswordResetToken(UUID.randomUUID().toString());
        user.setPasswordResetExpiresAt(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordReset(
                user.getEmail(),
                user.getFirstName(),
                user.getPasswordResetToken()
        );

        return new ApiResponseDTO(
                "If an account with that email exists, we've sent a password reset link."
        );
    }

    public ApiResponseDTO resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token);

        if (user == null) {
            throw new PasswordResetTokenInvalidException();
        }

        if (user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PasswordResetTokenExpiredException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        userRepository.save(user);

        return new ApiResponseDTO(
                "Password reset successfully!"
        );
    }

}
