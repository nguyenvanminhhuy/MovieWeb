package com.example.movie_backend.service;

import com.example.movie_backend.dto.response.MovieResponse;
import com.example.movie_backend.dto.response.PageResponse;

import java.util.List;

public interface FavoriteService {
    void addFavorite(String movieId);

    void removeFavorite(String movieId);

    PageResponse<MovieResponse> getMyFavorites(int page, int size);
}
