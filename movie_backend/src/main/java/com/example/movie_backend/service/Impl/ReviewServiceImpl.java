package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.ReviewRequest;
import com.example.movie_backend.dto.response.ReviewResponse;
import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.entity.Review;
import com.example.movie_backend.entity.User;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.ReviewMapper;
import com.example.movie_backend.repository.MovieRepository;
import com.example.movie_backend.repository.ReviewRepository;
import com.example.movie_backend.repository.UserRepository;
import com.example.movie_backend.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {
    ReviewRepository reviewRepository;
    MovieRepository movieRepository;
    UserRepository userRepository;
    ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        Review review = reviewMapper.toReview(request);
        review.setUser(user);
        review.setMovie(movie);

        Review savedReview = reviewRepository.save(review);

        // Update movie rating
        updateMovieRating(movie);

        return reviewMapper.toReviewResponse(savedReview);
    }

    private void updateMovieRating(Movie movie) {
        List<Review> reviews = reviewRepository.findByMovie(movie);
        if (reviews.isEmpty()) {
            movie.setRating(0.0);
        } else {
            double avgRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            movie.setRating(avgRating);
        }
        movieRepository.save(movie);
    }

    @Override
    public List<ReviewResponse> getByMovieId(String movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        return reviewRepository.findByMovie(movie).stream()
                .map(reviewMapper::toReviewResponse)
                .toList();
    }

    @Override
    public void delete(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isAdmin && !review.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Movie movie = review.getMovie();
        reviewRepository.deleteById(id);

        // Re-calculate rating after deletion
        updateMovieRating(movie);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
