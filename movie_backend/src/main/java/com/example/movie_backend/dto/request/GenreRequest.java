package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreRequest {
    @NotBlank(message = "NAME_REQUIRED")
    String name;
    String slug;
    String description;
}