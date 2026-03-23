package com.example.movie_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "franchises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Franchise extends BaseEntity {
    @Column(unique = true, nullable = false)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    String poster;

    @OneToMany(mappedBy = "franchise")
    List<Movie> movies;
}
