package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Episode;
import com.example.movie_backend.entity.User;
import com.example.movie_backend.entity.WatchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, String> {
    Page<WatchHistory> findByUser(User user, Pageable pageable);

    List<WatchHistory> findByUserOrderByUpdatedAtDesc(User user);

    Optional<WatchHistory> findByUserAndEpisode(User user, Episode episode);
}
