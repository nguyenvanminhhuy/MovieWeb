package com.example.movie_backend.repository;

import com.example.movie_backend.entity.VideoSource;
import com.example.movie_backend.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoSourceRepository extends JpaRepository<VideoSource, String> {
    List<VideoSource> findByEpisode(Episode episode);
}
