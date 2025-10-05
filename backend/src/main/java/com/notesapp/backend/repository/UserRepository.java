package com.notesapp.backend.repository;

import com.notesapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByEmailVerificationToken(String token);
    User findByPasswordResetToken(String token);
}
