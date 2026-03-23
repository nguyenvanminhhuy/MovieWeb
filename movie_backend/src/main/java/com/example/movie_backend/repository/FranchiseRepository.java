package com.example.movie_backend.repository;

import com.example.movie_backend.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, String> {
    Optional<Franchise> findByName(String name);
}
