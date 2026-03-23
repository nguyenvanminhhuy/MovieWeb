package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    Optional<Movie> findByTitle(String title);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE " +
            "(:query IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:genreId IS NULL OR g.id = :genreId) AND " +
            "(:franchiseId IS NULL OR m.franchise.id = :franchiseId) AND " +
            "(:type IS NULL OR m.type = :type) AND " +
            "(:status IS NULL OR m.status = :status)")
    Page<Movie> searchMovies(@Param("query") String query,
            @Param("genreId") String genreId,
            @Param("franchiseId") String franchiseId,
            @Param("type") MovieType type,
            @Param("status") MovieStatus status,
            Pageable pageable);
}
