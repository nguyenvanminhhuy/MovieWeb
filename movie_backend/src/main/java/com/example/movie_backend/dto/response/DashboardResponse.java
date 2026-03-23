package com.example.movie_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardResponse {
    long totalMovies;
    long totalUsers;
    long totalViews;
    long totalComments;
    Map<String, Long> moviesByType;
    Map<String, Long> moviesByStatus;
}
