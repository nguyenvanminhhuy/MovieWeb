package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Studio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudioRepository extends JpaRepository<Studio, String> {
    Optional<Studio> findByName(String name);
}
