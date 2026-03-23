package com.example.movie_backend.dto.response;

import com.example.movie_backend.enums.VideoQuality;
import com.example.movie_backend.enums.VideoType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoSourceResponse {
    String id;
    String url;
    VideoQuality quality;
    VideoType type;
}
