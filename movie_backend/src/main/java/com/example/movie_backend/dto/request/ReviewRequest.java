package com.example.movie_backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    @NotBlank(message = "MOVIE_ID_REQUIRED")
    String movieId;

    @NotNull(message = "RATING_INVALID")
    @Min(value = 1, message = "RATING_INVALID")
    @Max(value = 10, message = "RATING_INVALID")
    Integer rating;

    String content;
}