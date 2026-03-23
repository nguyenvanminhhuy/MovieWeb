package com.example.movie_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "episodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Episode extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "movie_id")
    Movie movie;

    Integer episodeNumber;
    String title;
    Integer duration; // in minutes

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL)
    List<VideoSource> videoSources;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL)
    List<Subtitle> subtitles;
}
