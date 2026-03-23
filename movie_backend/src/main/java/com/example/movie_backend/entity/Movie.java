package com.example.movie_backend.entity;

import com.example.movie_backend.enums.MovieStatus;
import com.example.movie_backend.enums.MovieType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie extends BaseEntity {
    String title;
    String originalTitle;
    @Column(columnDefinition = "TEXT")
    String description;
    String poster;
    String banner;
    String trailerUrl;

    @Enumerated(EnumType.STRING)
    MovieType type; // TV Series, Movie, OVA, Special

    @Enumerated(EnumType.STRING)
    MovieStatus status; // Ongoing, Completed, Upcoming

    Integer releaseYear;
    Integer totalEpisodes;
    Double rating;
    Long views;

    @ManyToMany
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<Genre> genres;

    @ManyToOne
    @JoinColumn(name = "studio_id")
    Studio studio;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    Franchise franchise;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    List<Episode> episodes;
}
