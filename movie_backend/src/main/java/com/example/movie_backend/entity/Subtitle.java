package com.example.movie_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "subtitles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subtitle extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "episode_id")
    Episode episode;

    String url;
    String language; // Vietnamese, English, etc.
}
