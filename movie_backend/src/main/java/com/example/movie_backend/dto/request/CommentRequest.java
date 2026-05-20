package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    @NotBlank(message = "MOVIE_ID_REQUIRED")
    String movieId;
    @NotBlank(message = "CONTENT_REQUIRED")
    String content;
    String parentId; // For replies
}