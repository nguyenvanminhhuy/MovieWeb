package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubtitleRequest {
    @NotBlank(message = "EPISODE_ID_REQUIRED")
    String episodeId;
    @NotBlank(message = "URL_REQUIRED")
    String url;
    @NotBlank(message = "LANGUAGE_REQUIRED")
    String language;
}