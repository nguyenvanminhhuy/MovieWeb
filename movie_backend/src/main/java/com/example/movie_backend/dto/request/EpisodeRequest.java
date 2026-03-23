package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeRequest {
    String movieId;
    Integer episodeNumber;
    String title;
    Integer duration;
}
