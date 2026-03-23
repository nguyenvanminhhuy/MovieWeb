package com.example.movie_backend.entity;

import com.example.movie_backend.enums.VideoQuality;
import com.example.movie_backend.enums.VideoType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "video_sources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoSource extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "episode_id")
    Episode episode;

    String url;

    @Enumerated(EnumType.STRING)
    VideoQuality quality; // 360p, 480p, 720p, 1080p

    @Enumerated(EnumType.STRING)
    VideoType type; // HLS, MP4, Embed
}
