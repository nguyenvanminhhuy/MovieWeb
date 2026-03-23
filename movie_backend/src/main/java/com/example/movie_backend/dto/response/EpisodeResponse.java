package com.example.movie_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeResponse {
    String id;
    Integer episodeNumber;
    String title;
    Integer duration;
    List<VideoSourceResponse> videoSources;
    List<SubtitleResponse> subtitles;
}
