package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.MovieRequest;
import com.example.movie_backend.dto.response.MovieResponse;
import com.example.movie_backend.dto.response.PageResponse;

import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;

public interface MovieService {
    MovieResponse create(MovieRequest request);

    PageResponse<MovieResponse> getAll(String query, String genreId, MovieType type, MovieStatus status, int page,
            int size);

    PageResponse<MovieResponse> search(String query, String genreId, String franchiseId, MovieType type,
            MovieStatus status, int page, int size);

    MovieResponse getById(String id);

    MovieResponse update(String id, MovieRequest request);

    void delete(String id);

    void incrementViews(String movieId);

    PageResponse<MovieResponse> getTopMovies(String type, int page, int size);

    PageResponse<MovieResponse> getRelatedMovies(String movieId, int page, int size);
}
