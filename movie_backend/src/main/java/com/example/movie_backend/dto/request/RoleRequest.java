package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @NotBlank(message = "NAME_REQUIRED")
    String name;
    String description;
    Set<String> permissions;
}