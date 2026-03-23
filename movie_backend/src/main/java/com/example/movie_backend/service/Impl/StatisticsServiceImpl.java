package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.response.DashboardResponse;
import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;
import com.example.movie_backend.repository.CommentRepository;
import com.example.movie_backend.repository.MovieRepository;
import com.example.movie_backend.repository.UserRepository;
import com.example.movie_backend.service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsServiceImpl implements StatisticsService {
    MovieRepository movieRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;

    @Override
    public DashboardResponse getDashboardStats() {
        long totalMovies = movieRepository.count();
        long totalUsers = userRepository.count();
        long totalComments = commentRepository.count();
        
        // Sum of all views in Movie table
        long totalViews = movieRepository.findAll().stream()
                .mapToLong(m -> m.getViews() != null ? m.getViews() : 0L)
                .sum();

        Map<String, Long> moviesByType = new HashMap<>();
        for (MovieType type : MovieType.values()) {
            moviesByType.put(type.name(), movieRepository.findAll().stream()
                    .filter(m -> m.getType() == type).count());
        }

        Map<String, Long> moviesByStatus = new HashMap<>();
        for (MovieStatus status : MovieStatus.values()) {
            moviesByStatus.put(status.name(), movieRepository.findAll().stream()
                    .filter(m -> m.getStatus() == status).count());
        }

        return DashboardResponse.builder()
                .totalMovies(totalMovies)
                .totalUsers(totalUsers)
                .totalComments(totalComments)
                .totalViews(totalViews)
                .moviesByType(moviesByType)
                .moviesByStatus(moviesByStatus)
                .build();
    }
}
