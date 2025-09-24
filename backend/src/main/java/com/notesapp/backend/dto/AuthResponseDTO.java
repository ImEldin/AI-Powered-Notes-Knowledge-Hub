package com.notesapp.backend.dto;

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
    private boolean emailVerified;

}
