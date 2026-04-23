package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.MovieRequest;
import com.example.movie_backend.dto.response.MovieResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;
import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.entity.MovieView;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.MovieMapper;
import com.example.movie_backend.repository.FranchiseRepository;
import com.example.movie_backend.repository.GenreRepository;
import com.example.movie_backend.repository.MovieRepository;
import com.example.movie_backend.repository.MovieViewRepository;
import com.example.movie_backend.repository.StudioRepository;
import com.example.movie_backend.service.AuditLogService;
import com.example.movie_backend.service.MovieService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieServiceImpl implements MovieService {
    MovieRepository movieRepository;
    GenreRepository genreRepository;
    StudioRepository studioRepository;
    FranchiseRepository franchiseRepository;
    MovieViewRepository movieViewRepository;
    AuditLogService auditLogService;
    MovieMapper movieMapper;

    @Override
    public MovieResponse create(MovieRequest request) {
        Movie movie = movieMapper.toMovie(request);

        if (request.getStudioId() != null) {
            var studio = studioRepository.findById(request.getStudioId())
                    .orElseThrow(() -> new AppException(ErrorCode.STUDIO_NOT_FOUND));
            movie.setStudio(studio);
        }

        if (request.getFranchiseId() != null) {
            var franchise = franchiseRepository.findById(request.getFranchiseId())
                    .orElseThrow(() -> new AppException(ErrorCode.FRANCHISE_NOT_FOUND));
            movie.setFranchise(franchise);
        }

        if (request.getGenreIds() != null) {
            var genres = genreRepository.findAllById(request.getGenreIds());
            movie.setGenres(new HashSet<>(genres));
        }

        Movie savedMovie = movieRepository.save(movie);
        auditLogService.log("CREATE_MOVIE", "Title: " + savedMovie.getTitle());

        return movieMapper.toMovieResponse(savedMovie);
    }

    @Override
    public PageResponse<MovieResponse> getAll(String query, String genreId, MovieType type, MovieStatus status, int page,
            int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = movieRepository.searchMovies(query, genreId, null, type, status, pageable);

        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(movieMapper::toMovieResponse).toList())
                .build();
    }

    @Override
    public PageResponse<MovieResponse> search(String query, String genreId, String franchiseId, MovieType type,
            MovieStatus status, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = movieRepository.searchMovies(query, genreId, franchiseId, type, status, pageable);

        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(movieMapper::toMovieResponse).toList())
                .build();
    }

    @Override
    public MovieResponse getById(String id) {
        return movieMapper.toMovieResponse(movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND)));
    }

    @Override
    public MovieResponse update(String id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        movieMapper.updateMovie(movie, request);

        if (request.getStudioId() != null) {
            var studio = studioRepository.findById(request.getStudioId())
                    .orElseThrow(() -> new AppException(ErrorCode.STUDIO_NOT_FOUND));
            movie.setStudio(studio);
        }

        if (request.getFranchiseId() != null) {
            var franchise = franchiseRepository.findById(request.getFranchiseId())
                    .orElseThrow(() -> new AppException(ErrorCode.FRANCHISE_NOT_FOUND));
            movie.setFranchise(franchise);
        }

        if (request.getGenreIds() != null) {
            var genres = genreRepository.findAllById(request.getGenreIds());
            movie.setGenres(new HashSet<>(genres));
        }

        Movie updatedMovie = movieRepository.save(movie);
        auditLogService.log("UPDATE_MOVIE", "ID: " + id + ", Title: " + updatedMovie.getTitle());

        return movieMapper.toMovieResponse(updatedMovie);
    }

    @Override
    public void delete(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        movieRepository.deleteById(id);
        auditLogService.log("DELETE_MOVIE", "ID: " + id + ", Title: " + movie.getTitle());
    }

    @Override
    @Transactional
    public void incrementViews(String movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        // Increment total views
        movie.setViews(movie.getViews() == null ? 1 : movie.getViews() + 1);
        movieRepository.save(movie);

        // Increment daily views
        LocalDate today = LocalDate.now();
        MovieView movieView = movieViewRepository.findByMovieAndViewDate(movie, today)
                .orElseGet(() -> {
                    MovieView newView = new MovieView();
                    newView.setMovie(movie);
                    newView.setViewDate(today);
                    newView.setViews(0L);
                    return newView;
                });
        movieView.setViews(movieView.getViews() + 1);
        movieViewRepository.save(movieView);
    }

    @Override
    public PageResponse<MovieResponse> getTopMovies(String type, int page, int size) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;

        switch (type.toLowerCase()) {
            case "day" -> startDate = now;
            case "week" -> startDate = now.minusWeeks(1);
            case "month" -> startDate = now.minusMonths(1);
            default -> {
                // All time (sorted by total views)
                Pageable pageable = PageRequest.of(page - 1, size, Sort.by("views").descending());
                var pageData = movieRepository.findAll(pageable);
                return PageResponse.<MovieResponse>builder()
                        .currentPage(page)
                        .pageSize(size)
                        .totalPages(pageData.getTotalPages())
                        .totalElements(pageData.getTotalElements())
                        .data(pageData.getContent().stream().map(movieMapper::toMovieResponse).toList())
                        .build();
            }
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        List<Object[]> results = movieViewRepository.findTopMoviesByViewsInRange(startDate, now, pageable);

        List<MovieResponse> data = results.stream()
                .map(result -> movieMapper.toMovieResponse((Movie) result[0]))
                .collect(Collectors.toList());

        // Note: For simplicity, totalPages and totalElements are estimated or
        // simplified here
        // In a real production app, you might want a more precise count query
        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(1) // Simplified
                .totalElements((long) data.size())
                .data(data)
                .build();
    }

    @Override
    public PageResponse<MovieResponse> getRelatedMovies(String movieId, int page, int size) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        // Find movies with same genres, excluding current movie
        List<String> genreIds = movie.getGenres().stream().map(g -> g.getId()).toList();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("views").descending());

        // Call repository to find movies sharing genres
        var pageData = movieRepository.findRelatedMovies(genreIds, movieId, pageable);

        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(movieMapper::toMovieResponse).toList())
                .build();
    }
}
