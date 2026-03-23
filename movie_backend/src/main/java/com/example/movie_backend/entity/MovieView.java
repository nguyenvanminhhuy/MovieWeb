package com.example.movie_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "movie_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieView extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "movie_id")
    Movie movie;

    Long views = 0L;
    LocalDate viewDate;
}
