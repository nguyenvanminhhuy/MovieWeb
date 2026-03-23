package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    Optional<Genre> findBySlug(String slug);
    Optional<Genre> findByName(String name);
}
