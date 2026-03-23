package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.entity.MovieView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieViewRepository extends JpaRepository<MovieView, String> {
    Optional<MovieView> findByMovieAndViewDate(Movie movie, LocalDate viewDate);

    @Query("SELECT mv.movie, SUM(mv.views) as totalViews FROM MovieView mv " +
           "WHERE mv.viewDate BETWEEN :startDate AND :endDate " +
           "GROUP BY mv.movie " +
           "ORDER BY totalViews DESC")
    List<Object[]> findTopMoviesByViewsInRange(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate, 
                                               Pageable pageable);
}
