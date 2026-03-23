package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.entity.Review;
import com.example.movie_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByMovie(Movie movie);
    Optional<Review> findByMovieAndUser(Movie movie, User user);
}
