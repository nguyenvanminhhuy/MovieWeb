package com.example.movie_backend.dto.request;

import com.example.movie_backend.enums.VideoQuality;
import com.example.movie_backend.enums.VideoType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoSourceRequest {
    String episodeId;
    String url;
    VideoQuality quality;
    VideoType type;
}
