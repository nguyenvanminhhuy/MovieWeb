package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Favorite;
import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    Page<Favorite> findByUser(User user, Pageable pageable);

    List<Favorite> findByUser(User user);

    List<Favorite> findByMovie(Movie movie);

    Optional<Favorite> findByUserAndMovie(User user, Movie movie);
}
