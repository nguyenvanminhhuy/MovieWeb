package com.example.movie_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse {
    String id;
    UserResponse user;
    MovieResponse movie;
    EpisodeResponse episode;
    String reason;
    String description;
    boolean resolved;
    java.time.LocalDateTime createdAt;
}
