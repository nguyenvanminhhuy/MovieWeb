package com.example.movie_backend.dto.response;

import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    String id;
    String title;
    String originalTitle;
    String description;
    String poster;
    String banner;
    String trailerUrl;
    MovieType type;
    MovieStatus status;
    Integer releaseYear;
    Integer totalEpisodes;
    Double rating;
    Long views;
    StudioResponse studio;
    FranchiseResponse franchise;
    Set<GenreResponse> genres;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
