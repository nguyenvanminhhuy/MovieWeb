package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeRequest {
    @NotBlank(message = "MOVIE_ID_REQUIRED")
    String movieId;
    @NotNull(message = "EPISODE_NUMBER_INVALID")
    Integer episodeNumber;
    String title;
    Integer duration;
}