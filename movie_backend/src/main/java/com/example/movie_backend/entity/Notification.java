package com.example.movie_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    String title;
    String message;
    String type; // e.g., NEW_EPISODE, SYSTEM, PROMOTION
    String targetUrl; // Link to the movie/episode
    boolean read = false;
}
