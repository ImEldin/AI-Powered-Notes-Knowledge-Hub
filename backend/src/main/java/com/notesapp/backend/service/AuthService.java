package com.notesapp.backend.service;

import com.notesapp.backend.dto.AuthResponseDTO;
import com.notesapp.backend.dto.LoginRequestDTO;
import com.notesapp.backend.dto.RegisterRequestDTO;
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

    public AuthResponseDTO register(RegisterRequestDTO request, HttpServletResponse response){

        if(userRepository.findByEmail(request.getEmail()) != null){
            throw new RuntimeException("Email already in use!");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName()
        );

        user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setEmailVerificationExpiresAt(LocalDateTime.now().plusHours(24));

        User savedUser = userRepository.save(user);

        String jwt = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());

        cookieUtil.addJwtCookie(response, jwt);
        // TODO: Send verification email later
        return new AuthResponseDTO(
                "Registration successful",
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
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        if (user.isLocked() && user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is temporarily locked. Try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= 5) {
                user.setLocked(true);
                user.setLockedUntil(LocalDateTime.now().plusHours(1));
            }

            userRepository.save(user);
            throw new RuntimeException("Invalid email or password");
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

    public void logout(HttpServletResponse response){
        cookieUtil.clearJwtCookie(response);
    }

}
