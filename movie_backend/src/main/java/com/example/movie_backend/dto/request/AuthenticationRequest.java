package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @NotBlank(message = "USERNAME_REQUIRED")
    String username;
    @NotBlank(message = "PASSWORD_REQUIRED")
    String password;
}