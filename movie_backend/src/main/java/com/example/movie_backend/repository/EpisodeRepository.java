package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Episode;
import com.example.movie_backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, String> {
    List<Episode> findByMovie(Movie movie);
    Optional<Episode> findByMovieAndEpisodeNumber(Movie movie, Integer episodeNumber);
}
