# Database Entity Definitions

This document describes the database tables and fields used in the MovieWeb backend. This information is intended for the Front-End (FE) team to understand the data structure and relationships.

## Common Fields (BaseEntity)

Most entities extend `BaseEntity`, meaning they automatically include these fields:

| Field | Type | Description |
|---|---|---|
| `id` | `String (UUID)` | Primary Key (generated automatically) |
| `createdAt` | `LocalDateTime` | Record creation timestamp |
| `updatedAt` | `LocalDateTime` | Record last update timestamp |

---

## Entity Details

### 1. AuditLog (Table: `audit_logs`)
Logs administrative actions.
| Field | Type | Description |
|---|---|---|
| `username` | `String` | User who performed the action |
| `action` | `String` | Action type |
| `details` | `String` | Detailed description |
| `ipAddress` | `String` | IP address of the user |

### 2. Comment (Table: `comments`)
User comments on movies.
| Field | Type | Description |
|---|---|---|
| `user` | `User` | The user who commented |
| `movie` | `Movie` | The movie being commented on |
| `content` | `String (TEXT)` | Comment content |
| `parent` | `Comment` | Parent comment (for replies) |
| `replies` | `List<Comment>` | List of reply comments |
| `likes` | `Integer` | Number of likes |

### 3. Episode (Table: `episodes`)
Episodes of a TV Series or Movie.
| Field | Type | Description |
|---|---|---|
| `movie` | `Movie` | The movie this episode belongs to |
| `episodeNumber` | `Integer` | Episode number |
| `title` | `String` | Episode title |
| `duration` | `Integer` | Duration in minutes |
| `videoSources` | `List<VideoSource>`| Links to video files |
| `subtitles` | `List<Subtitle>` | Subtitle files |

### 4. Favorite (Table: `favorites`)
User's favorite movies.
| Field | Type | Description |
|---|---|---|
| `user` | `User` | The user |
| `movie` | `Movie` | The favorite movie |

### 5. Franchise (Table: `franchises`)
Series of related movies (e.g., Marvel Cinematic Universe).
| Field | Type | Description |
|---|---|---|
| `name` | `String` | Franchise name |
| `description` | `String (TEXT)` | Description |
| `poster` | `String` | URL to poster image |
| `movies` | `List<Movie>` | Movies in this franchise |

### 6. Genre (Table: `genres`)
Movie genres (e.g., Action, Comedy).
| Field | Type | Description |
|---|---|---|
| `name` | `String` | Genre name |
| `slug` | `String` | URL-friendly name |
| `description` | `String` | Description |

### 7. InvalidatedToken
Used for authentication (blacklisted tokens). *Note: Does not extend BaseEntity.*
| Field | Type | Description |
|---|---|---|
| `id` | `String` | Token ID |
| `expiryTime` | `Date` | When the token expires |

### 8. Movie (Table: `movies`)
Core movie/series entity.
| Field | Type | Description |
|---|---|---|
| `title` | `String` | Main title |
| `originalTitle` | `String` | Original title (e.g., in Japanese) |
| `description` | `String (TEXT)` | Summary |
| `poster` | `String` | Poster image URL |
| `banner` | `String` | Banner image URL |
| `trailerUrl` | `String` | YouTube/Trailer URL |
| `type` | `Enum (MovieType)` | TV_SERIES, MOVIE, OVA, SPECIAL |
| `status` | `Enum (MovieStatus)`| ONGOING, COMPLETED, UPCOMING |
| `releaseYear` | `Integer` | Year released |
| `totalEpisodes` | `Integer` | Max episodes |
| `rating` | `Double` | Average rating |
| `views` | `Long` | Total view count |
| `genres` | `Set<Genre>` | Genres |
| `studio` | `Studio` | Production studio |
| `franchise` | `Franchise` | Belonging franchise |
| `episodes` | `List<Episode>` | List of episodes |

### 9. MovieView (Table: `movie_views`)
Daily view tracking for analytics.
| Field | Type | Description |
|---|---|---|
| `movie` | `Movie` | The movie |
| `views` | `Long` | Views on a specific date |
| `viewDate` | `LocalDate` | The date of these views |

### 10. Notification (Table: `notifications`)
User notifications.
| Field | Type | Description |
|---|---|---|
| `user` | `User` | Recipient |
| `title` | `String` | Notification title |
| `message` | `String` | Content |
| `type` | `String` | NEW_EPISODE, SYSTEM, etc. |
| `targetUrl` | `String` | Link to action |
| `read` | `boolean` | Read status |

### 11. Permissions (Table: `permissions`)
System permissions for RBAC.
| Field | Type | Description |
|---|---|---|
| `name` | `String` | Permission name |
| `description` | `String` | Description |

### 12. Report (Table: `reports`)
User reports for broken links or issues.
| Field | Type | Description |
|---|---|---|
| `user` | `User` | Reporter |
| `movie` | `Movie` | Reported movie |
| `episode` | `Episode` | Reported episode |
| `reason` | `String` | Category/Reason |
| `description` | `String (TEXT)` | Details |
| `resolved` | `boolean` | Status |

### 13. Review (Table: `reviews`)
User ratings and reviews.
| Field | Type | Description |
|---|---|---|
| `user` | `User` | Reviewer |
| `movie` | `Movie` | Reviewed movie |
| `rating` | `Integer` | 1-10 rating |
| `content` | `String (TEXT)` | Written review |

### 14. Role (Table: `roles`)
User roles (e.g., ADMIN, USER).
| Field | Type | Description |
|---|---|---|
| `name` | `String` | Role name |
| `description` | `String` | Description |
| `permissions`| `Set<Permissions>`| Set of permissions |

### 15. Studio (Table: `studios`)
Animation/Production studios.
| Field | Type | Description |
|---|---|---|
| `name` | `String` | Studio name |
| `description` | `String` | Description |
| `logo` | `String` | Logo URL |

### 16. Subtitle (Table: `subtitles`)
| Field | Type | Description |
|---|---|---|
| `episode` | `Episode` | Parent episode |
| `url` | `String` | Link to .vtt/.srt file |
| `language` | `String` | e.g., Vietnamese, English |

### 17. User (Table: `users`)
| Field | Type | Description |
|---|---|---|
| `username` | `String (Unique)` | Login name |
| `password` | `String` | Hashed password |
| `email` | `String (Unique)` | Email address |
| `fullName` | `String` | Display name |
| `avatar` | `String` | Profile picture URL |
| `enabled` | `boolean` | Account status |
| `roles` | `Set<Role>` | Set of roles |

### 18. VideoSource (Table: `video_sources`)
| Field | Type | Description |
|---|---|---|
| `episode` | `Episode` | Parent episode |
| `url` | `String` | Link to video |
| `quality` | `Enum (VideoQuality)`| 360p, 480p, 720p, 1080p |
| `type` | `Enum (VideoType)` | HLS, MP4, EMBED |

### 19. WatchHistory (Table: `watch_history`)
| Field | Type | Description |
|---|---|---|
| `user` | `User` | The user |
| `episode` | `Episode` | The episode watched |
| `progress` | `Integer` | Progress in seconds |
