package com.example.movie_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryRequest {
    @NotBlank(message = "EPISODE_ID_REQUIRED")
    String episodeId;
    @NotNull(message = "PROGRESS_INVALID")
    @Min(value = 0, message = "PROGRESS_INVALID")
    Integer progress; // in seconds
}