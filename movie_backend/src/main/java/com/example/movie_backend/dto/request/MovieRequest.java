package com.example.movie_backend.dto.request;

import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRequest {
    @NotBlank(message = "TITLE_INVALID")
    String title;
    String originalTitle;
    String description;
    String poster;
    String banner;
    String trailerUrl;

    @NotNull(message = "TYPE_INVALID")
    MovieType type;

    @NotNull(message = "STATUS_INVALID")
    MovieStatus status;

    Integer releaseYear;
    Integer totalEpisodes;
    String studioId;
    String franchiseId;
    Set<String> genreIds;
}
