package com.notesapp.backend.dto;

import com.notesapp.backend.model.UserRole;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String message;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean emailVerified;

}
