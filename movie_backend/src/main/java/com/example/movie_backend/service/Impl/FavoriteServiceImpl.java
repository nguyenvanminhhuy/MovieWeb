package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.response.MovieResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.entity.Favorite;
import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.entity.User;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.MovieMapper;
import com.example.movie_backend.repository.FavoriteRepository;
import com.example.movie_backend.repository.MovieRepository;
import com.example.movie_backend.repository.UserRepository;
import com.example.movie_backend.service.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteServiceImpl implements FavoriteService {
    FavoriteRepository favoriteRepository;
    MovieRepository movieRepository;
    UserRepository userRepository;
    MovieMapper movieMapper;

    @Override
    public void addFavorite(String movieId) {
        User user = getCurrentUser();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        if (favoriteRepository.findByUserAndMovie(user, movie).isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setMovie(movie);
            favoriteRepository.save(favorite);
        }
    }

    @Override
    public void removeFavorite(String movieId) {
        User user = getCurrentUser();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        favoriteRepository.findByUserAndMovie(user, movie)
                .ifPresent(favoriteRepository::delete);
    }

    @Override
    public PageResponse<MovieResponse> getMyFavorites(int page, int size) {
        User user = getCurrentUser();
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = favoriteRepository.findByUser(user, pageable);

        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(favorite -> movieMapper.toMovieResponse(favorite.getMovie()))
                        .toList())
                .build();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
