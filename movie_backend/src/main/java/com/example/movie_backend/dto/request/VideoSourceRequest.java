package com.example.movie_backend.dto.request;

import com.example.movie_backend.enums.VideoQuality;
import com.example.movie_backend.enums.VideoType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoSourceRequest {
    @NotBlank(message = "INVALID_KEY") String episodeId;
    @NotBlank(message = "INVALID_KEY") String url;
    @NotNull(message = "INVALID_KEY") VideoQuality quality;
    @NotNull(message = "INVALID_KEY") VideoType type;
}