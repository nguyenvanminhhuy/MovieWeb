package com.example.movie_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryResponse {
    String id;
    MovieResponse movie;
    EpisodeResponse episode;
    Integer progress;
    LocalDateTime updatedAt;
}
