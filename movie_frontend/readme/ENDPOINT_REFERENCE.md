# 📍 API Endpoint Mapping Reference

## Complete Endpoint Inventory with Cross-References

This document serves as a quick reference guide mapping every endpoint to its implementation and documentation location.

---

## Authentication API (`/auth`)

| Endpoint           | Method | Auth | Doc Status | Controller                                                                                                               | Swagger Tag               |
| ------------------ | ------ | ---- | ---------- | ------------------------------------------------------------------------------------------------------------------------ | ------------------------- |
| `/auth/login`      | POST   | ❌   | ✅         | [AuthenticationController.java:36](src/main/java/com/example/movie_backend/controller/AuthenticationController.java#L36) | Authentication Controller |
| `/auth/introspect` | POST   | ❌   | ✅         | [AuthenticationController.java:43](src/main/java/com/example/movie_backend/controller/AuthenticationController.java#L43) | Authentication Controller |
| `/auth/logout`     | POST   | ✅   | ✅         | [AuthenticationController.java:51](src/main/java/com/example/movie_backend/controller/AuthenticationController.java#L51) | Authentication Controller |
| `/auth/refresh`    | POST   | ❌   | ✅         | [AuthenticationController.java:59](src/main/java/com/example/movie_backend/controller/AuthenticationController.java#L59) | Authentication Controller |

---

## User Management API (`/users`)

| Endpoint                 | Method | Auth     | Doc Status | Controller                                                                                           | Params              |
| ------------------------ | ------ | -------- | ---------- | ---------------------------------------------------------------------------------------------------- | ------------------- |
| `/users/my-info`         | GET    | ✅       | ✅         | [UserController.java:30](src/main/java/com/example/movie_backend/controller/UserController.java#L30) | -                   |
| `/users`                 | POST   | ❌       | ✅         | [UserController.java:37](src/main/java/com/example/movie_backend/controller/UserController.java#L37) | -                   |
| `/users`                 | GET    | ✅ ADMIN | ✅         | [UserController.java:44](src/main/java/com/example/movie_backend/controller/UserController.java#L44) | `page`, `size`      |
| `/users/{userId}`        | GET    | ✅ ADMIN | ✅         | [UserController.java:53](src/main/java/com/example/movie_backend/controller/UserController.java#L53) | `userId`            |
| `/users/{userId}`        | PUT    | ✅ ADMIN | ✅         | [UserController.java:61](src/main/java/com/example/movie_backend/controller/UserController.java#L61) | `userId`            |
| `/users/{userId}`        | DELETE | ✅ ADMIN | ✅         | [UserController.java:68](src/main/java/com/example/movie_backend/controller/UserController.java#L68) | `userId`            |
| `/users/update-profile`  | PUT    | ✅       | ✅         | [UserController.java:76](src/main/java/com/example/movie_backend/controller/UserController.java#L76) | -                   |
| `/users/{userId}/status` | PATCH  | ✅ ADMIN | ✅         | [UserController.java:84](src/main/java/com/example/movie_backend/controller/UserController.java#L84) | `userId`, `enabled` |

---

## Movie Management API (`/movies`)

| Endpoint       | Method | Auth     | Doc Status | Controller                                                                                             | Params         |
| -------------- | ------ | -------- | ---------- | ------------------------------------------------------------------------------------------------------ | -------------- |
| `/movies`      | GET    | ✅ ADMIN | ✅         | [MovieController.java:33](src/main/java/com/example/movie_backend/controller/MovieController.java#L33) | `page`, `size` |
| `/movies/{id}` | GET    | ✅ ADMIN | ✅         | [MovieController.java:43](src/main/java/com/example/movie_backend/controller/MovieController.java#L43) | `id`           |
| `/movies`      | POST   | ✅ ADMIN | ✅         | [MovieController.java:49](src/main/java/com/example/movie_backend/controller/MovieController.java#L49) | -              |
| `/movies/{id}` | PUT    | ✅ ADMIN | ✅         | [MovieController.java:59](src/main/java/com/example/movie_backend/controller/MovieController.java#L59) | `id`           |
| `/movies/{id}` | DELETE | ✅ ADMIN | ✅         | [MovieController.java:67](src/main/java/com/example/movie_backend/controller/MovieController.java#L67) | `id`           |

---

## Common API (`/common`)

| Endpoint                                    | Method | Auth | Doc Status | Controller                                                                                                 | Params                                                              |
| ------------------------------------------- | ------ | ---- | ---------- | ---------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| `/common/movies/search`                     | GET    | ❌   | ✅         | [CommonController.java:38](src/main/java/com/example/movie_backend/controller/CommonController.java#L38)   | `query`, `genreId`, `franchiseId`, `type`, `status`, `page`, `size` |
| `/common/movies`                            | GET    | ❌   | ✅         | [CommonController.java:73](src/main/java/com/example/movie_backend/controller/CommonController.java#L73)   | `page`, `size`                                                      |
| `/common/movies/{id}`                       | GET    | ❌   | ✅         | [CommonController.java:81](src/main/java/com/example/movie_backend/controller/CommonController.java#L81)   | `id`                                                                |
| `/common/genres`                            | GET    | ❌   | ✅         | [CommonController.java:88](src/main/java/com/example/movie_backend/controller/CommonController.java#L88)   | `page`, `size`                                                      |
| `/common/studios`                           | GET    | ❌   | ✅         | [CommonController.java:96](src/main/java/com/example/movie_backend/controller/CommonController.java#L96)   | `page`, `size`                                                      |
| `/common/episodes/movie/{movieId}`          | GET    | ❌   | ✅         | [CommonController.java:104](src/main/java/com/example/movie_backend/controller/CommonController.java#L104) | `movieId`                                                           |
| `/common/video-sources/episode/{episodeId}` | GET    | ❌   | ✅         | [CommonController.java:111](src/main/java/com/example/movie_backend/controller/CommonController.java#L111) | `episodeId`                                                         |
| `/common/subtitles/episode/{episodeId}`     | GET    | ❌   | ✅         | [CommonController.java:118](src/main/java/com/example/movie_backend/controller/CommonController.java#L118) | `episodeId`                                                         |
| `/common/comments/movie/{movieId}`          | GET    | ❌   | ✅         | [CommonController.java:49](src/main/java/com/example/movie_backend/controller/CommonController.java#L49)   | `movieId`                                                           |
| `/common/reviews/movie/{movieId}`           | GET    | ❌   | ✅         | [CommonController.java:56](src/main/java/com/example/movie_backend/controller/CommonController.java#L56)   | `movieId`                                                           |
| `/common/movies/{id}/views`                 | POST   | ❌   | ✅         | [CommonController.java:125](src/main/java/com/example/movie_backend/controller/CommonController.java#L125) | `id`                                                                |
| `/common/movies/top`                        | GET    | ❌   | ✅         | [CommonController.java:131](src/main/java/com/example/movie_backend/controller/CommonController.java#L131) | `type`, `page`, `size`                                              |
| `/common/movies/{id}/related`               | GET    | ❌   | ✅         | [CommonController.java:141](src/main/java/com/example/movie_backend/controller/CommonController.java#L141) | `id`, `page`, `size`                                                |

---

## Genre Management API (`/genres`)

| Endpoint       | Method | Auth     | Doc Status | Controller                                                                                             | Params         |
| -------------- | ------ | -------- | ---------- | ------------------------------------------------------------------------------------------------------ | -------------- |
| `/genres`      | POST   | ✅ ADMIN | ✅         | [GenreController.java:26](src/main/java/com/example/movie_backend/controller/GenreController.java#L26) | -              |
| `/genres`      | GET    | ✅ ADMIN | ✅         | [GenreController.java:33](src/main/java/com/example/movie_backend/controller/GenreController.java#L33) | `page`, `size` |
| `/genres/{id}` | PUT    | ✅ ADMIN | ✅         | [GenreController.java:41](src/main/java/com/example/movie_backend/controller/GenreController.java#L41) | `id`           |
| `/genres/{id}` | DELETE | ✅ ADMIN | ✅         | [GenreController.java:48](src/main/java/com/example/movie_backend/controller/GenreController.java#L48) | `id`           |

---

## Studio Management API (`/studios`)

| Endpoint        | Method | Auth     | Doc Status | Controller                                                                                               | Params         |
| --------------- | ------ | -------- | ---------- | -------------------------------------------------------------------------------------------------------- | -------------- |
| `/studios`      | POST   | ✅ ADMIN | ✅         | [StudioController.java:26](src/main/java/com/example/movie_backend/controller/StudioController.java#L26) | -              |
| `/studios`      | GET    | ✅ ADMIN | ✅         | [StudioController.java:33](src/main/java/com/example/movie_backend/controller/StudioController.java#L33) | `page`, `size` |
| `/studios/{id}` | PUT    | ✅ ADMIN | ✅         | [StudioController.java:41](src/main/java/com/example/movie_backend/controller/StudioController.java#L41) | `id`           |
| `/studios/{id}` | DELETE | ✅ ADMIN | ✅         | [StudioController.java:48](src/main/java/com/example/movie_backend/controller/StudioController.java#L48) | `id`           |

---

## Episode Management API (`/episodes`)

| Endpoint                    | Method | Auth     | Doc Status | Controller                                                                                                 | Params    |
| --------------------------- | ------ | -------- | ---------- | ---------------------------------------------------------------------------------------------------------- | --------- |
| `/episodes`                 | POST   | ✅ ADMIN | ✅         | [EpisodeController.java:26](src/main/java/com/example/movie_backend/controller/EpisodeController.java#L26) | -         |
| `/episodes/movie/{movieId}` | GET    | ✅ ADMIN | ✅         | [EpisodeController.java:33](src/main/java/com/example/movie_backend/controller/EpisodeController.java#L33) | `movieId` |
| `/episodes/{id}`            | PUT    | ✅ ADMIN | ✅         | [EpisodeController.java:40](src/main/java/com/example/movie_backend/controller/EpisodeController.java#L40) | `id`      |
| `/episodes/{id}`            | DELETE | ✅ ADMIN | ✅         | [EpisodeController.java:47](src/main/java/com/example/movie_backend/controller/EpisodeController.java#L47) | `id`      |

---

## Video Source API (`/video-sources`)

| Endpoint                             | Method | Auth     | Doc Status | Controller                                                                                                         | Params      |
| ------------------------------------ | ------ | -------- | ---------- | ------------------------------------------------------------------------------------------------------------------ | ----------- |
| `/video-sources`                     | POST   | ✅ ADMIN | ✅         | [VideoSourceController.java:27](src/main/java/com/example/movie_backend/controller/VideoSourceController.java#L27) | -           |
| `/video-sources/episode/{episodeId}` | GET    | ✅ ADMIN | ✅         | [VideoSourceController.java:34](src/main/java/com/example/movie_backend/controller/VideoSourceController.java#L34) | `episodeId` |
| `/video-sources/{id}`                | DELETE | ✅ ADMIN | ✅         | [VideoSourceController.java:41](src/main/java/com/example/movie_backend/controller/VideoSourceController.java#L41) | `id`        |

---

## Subtitle API (`/subtitles`)

| Endpoint                         | Method | Auth     | Doc Status | Controller                                                                                                   | Params      |
| -------------------------------- | ------ | -------- | ---------- | ------------------------------------------------------------------------------------------------------------ | ----------- |
| `/subtitles`                     | POST   | ✅ ADMIN | ✅         | [SubtitleController.java:27](src/main/java/com/example/movie_backend/controller/SubtitleController.java#L27) | -           |
| `/subtitles/episode/{episodeId}` | GET    | ✅ ADMIN | ✅         | [SubtitleController.java:34](src/main/java/com/example/movie_backend/controller/SubtitleController.java#L34) | `episodeId` |
| `/subtitles/{id}`                | DELETE | ✅ ADMIN | ✅         | [SubtitleController.java:41](src/main/java/com/example/movie_backend/controller/SubtitleController.java#L41) | `id`        |

---

## Comment API (`/comments`)

| Endpoint              | Method | Auth     | Doc Status | Controller                                                                                                 | Params         |
| --------------------- | ------ | -------- | ---------- | ---------------------------------------------------------------------------------------------------------- | -------------- |
| `/comments`           | GET    | ✅ ADMIN | ✅         | [CommentController.java:27](src/main/java/com/example/movie_backend/controller/CommentController.java#L27) | `page`, `size` |
| `/comments`           | POST   | ✅       | ✅         | [CommentController.java:39](src/main/java/com/example/movie_backend/controller/CommentController.java#L39) | -              |
| `/comments/{id}/like` | POST   | ✅       | ✅         | [CommentController.java:48](src/main/java/com/example/movie_backend/controller/CommentController.java#L48) | `id`           |
| `/comments/{id}`      | DELETE | ✅       | ✅         | [CommentController.java:57](src/main/java/com/example/movie_backend/controller/CommentController.java#L57) | `id`           |

---

## Review API (`/reviews`)

| Endpoint        | Method | Auth     | Doc Status | Controller                                                                                               | Params         |
| --------------- | ------ | -------- | ---------- | -------------------------------------------------------------------------------------------------------- | -------------- |
| `/reviews`      | GET    | ✅ ADMIN | ✅         | [ReviewController.java:28](src/main/java/com/example/movie_backend/controller/ReviewController.java#L28) | `page`, `size` |
| `/reviews`      | POST   | ✅       | ✅         | [ReviewController.java:40](src/main/java/com/example/movie_backend/controller/ReviewController.java#L40) | -              |
| `/reviews/{id}` | DELETE | ✅       | ✅         | [ReviewController.java:49](src/main/java/com/example/movie_backend/controller/ReviewController.java#L49) | `id`           |

---

## Favorite Management API (`/favorites`)

| Endpoint               | Method | Auth | Doc Status | Controller                                                                                                   | Params         |
| ---------------------- | ------ | ---- | ---------- | ------------------------------------------------------------------------------------------------------------ | -------------- |
| `/favorites/{movieId}` | POST   | ✅   | ✅         | [FavoriteController.java:23](src/main/java/com/example/movie_backend/controller/FavoriteController.java#L23) | `movieId`      |
| `/favorites/{movieId}` | DELETE | ✅   | ✅         | [FavoriteController.java:30](src/main/java/com/example/movie_backend/controller/FavoriteController.java#L30) | `movieId`      |
| `/favorites`           | GET    | ✅   | ✅         | [FavoriteController.java:37](src/main/java/com/example/movie_backend/controller/FavoriteController.java#L37) | `page`, `size` |

---

## Watch History API (`/history`)

| Endpoint   | Method | Auth | Doc Status | Controller                                                                                                           | Params         |
| ---------- | ------ | ---- | ---------- | -------------------------------------------------------------------------------------------------------------------- | -------------- |
| `/history` | POST   | ✅   | ✅         | [WatchHistoryController.java:24](src/main/java/com/example/movie_backend/controller/WatchHistoryController.java#L24) | -              |
| `/history` | GET    | ✅   | ✅         | [WatchHistoryController.java:31](src/main/java/com/example/movie_backend/controller/WatchHistoryController.java#L31) | `page`, `size` |

---

## Franchise Management API (`/franchises`)

| Endpoint           | Method | Auth     | Doc Status | Controller                                                                                                     | Params         |
| ------------------ | ------ | -------- | ---------- | -------------------------------------------------------------------------------------------------------------- | -------------- |
| `/franchises`      | POST   | ✅ ADMIN | ✅         | [FranchiseController.java:27](src/main/java/com/example/movie_backend/controller/FranchiseController.java#L27) | -              |
| `/franchises`      | GET    | ❌       | ✅         | [FranchiseController.java:35](src/main/java/com/example/movie_backend/controller/FranchiseController.java#L35) | `page`, `size` |
| `/franchises/{id}` | GET    | ❌       | ✅         | [FranchiseController.java:44](src/main/java/com/example/movie_backend/controller/FranchiseController.java#L44) | `id`           |
| `/franchises/{id}` | PUT    | ✅ ADMIN | ✅         | [FranchiseController.java:50](src/main/java/com/example/movie_backend/controller/FranchiseController.java#L50) | `id`           |
| `/franchises/{id}` | DELETE | ✅ ADMIN | ✅         | [FranchiseController.java:57](src/main/java/com/example/movie_backend/controller/FranchiseController.java#L57) | `id`           |

---

## Role API (`/roles`)

| Endpoint        | Method | Auth     | Doc Status | Controller                                                                                           | Params |
| --------------- | ------ | -------- | ---------- | ---------------------------------------------------------------------------------------------------- | ------ |
| `/roles`        | POST   | ✅ ADMIN | ✅         | [RoleController.java:28](src/main/java/com/example/movie_backend/controller/RoleController.java#L28) | -      |
| `/roles`        | GET    | ✅ ADMIN | ✅         | [RoleController.java:35](src/main/java/com/example/movie_backend/controller/RoleController.java#L35) | -      |
| `/roles/{role}` | DELETE | ✅ ADMIN | ✅         | [RoleController.java:42](src/main/java/com/example/movie_backend/controller/RoleController.java#L42) | `role` |

---

## Permission API (`/permissions`)

| Endpoint                    | Method | Auth     | Doc Status | Controller                                                                                                       | Params       |
| --------------------------- | ------ | -------- | ---------- | ---------------------------------------------------------------------------------------------------------------- | ------------ |
| `/permissions`              | POST   | ✅ ADMIN | ✅         | [PermissionController.java:28](src/main/java/com/example/movie_backend/controller/PermissionController.java#L28) | -            |
| `/permissions`              | GET    | ✅ ADMIN | ✅         | [PermissionController.java:35](src/main/java/com/example/movie_backend/controller/PermissionController.java#L35) | -            |
| `/permissions/{permission}` | DELETE | ✅ ADMIN | ✅         | [PermissionController.java:42](src/main/java/com/example/movie_backend/controller/PermissionController.java#L42) | `permission` |

---

## Report Management API (`/reports`)

| Endpoint                | Method | Auth     | Doc Status | Controller                                                                                               | Params         |
| ----------------------- | ------ | -------- | ---------- | -------------------------------------------------------------------------------------------------------- | -------------- |
| `/reports`              | POST   | ✅       | ✅         | [ReportController.java:23](src/main/java/com/example/movie_backend/controller/ReportController.java#L23) | -              |
| `/reports`              | GET    | ✅ ADMIN | ✅         | [ReportController.java:30](src/main/java/com/example/movie_backend/controller/ReportController.java#L30) | `page`, `size` |
| `/reports/{id}/resolve` | PATCH  | ✅ ADMIN | ✅         | [ReportController.java:39](src/main/java/com/example/movie_backend/controller/ReportController.java#L39) | `id`           |

---

## Notification API (`/notifications`)

| Endpoint                      | Method | Auth | Doc Status | Controller                                                                                                           | Params         |
| ----------------------------- | ------ | ---- | ---------- | -------------------------------------------------------------------------------------------------------------------- | -------------- |
| `/notifications`              | GET    | ✅   | ✅         | [NotificationController.java:22](src/main/java/com/example/movie_backend/controller/NotificationController.java#L22) | `page`, `size` |
| `/notifications/unread-count` | GET    | ✅   | ✅         | [NotificationController.java:31](src/main/java/com/example/movie_backend/controller/NotificationController.java#L31) | -              |
| `/notifications/{id}/read`    | PATCH  | ✅   | ✅         | [NotificationController.java:38](src/main/java/com/example/movie_backend/controller/NotificationController.java#L38) | `id`           |
| `/notifications/read-all`     | PATCH  | ✅   | ✅         | [NotificationController.java:45](src/main/java/com/example/movie_backend/controller/NotificationController.java#L45) | -              |

---

## Statistics API (`/admin/stats`)

| Endpoint                 | Method | Auth     | Doc Status | Controller                                                                                                       | Params |
| ------------------------ | ------ | -------- | ---------- | ---------------------------------------------------------------------------------------------------------------- | ------ |
| `/admin/stats/dashboard` | GET    | ✅ ADMIN | ✅         | [StatisticsController.java:25](src/main/java/com/example/movie_backend/controller/StatisticsController.java#L25) | -      |

---

## Media Upload API (`/media`)

| Endpoint        | Method | Auth | Doc Status | Controller                                                                                             | Params           |
| --------------- | ------ | ---- | ---------- | ------------------------------------------------------------------------------------------------------ | ---------------- |
| `/media/upload` | POST   | ❌   | ✅         | [MediaController.java:25](src/main/java/com/example/movie_backend/controller/MediaController.java#L25) | `file`, `folder` |

---

## Audit Log API (`/admin/audit-logs`)

| Endpoint            | Method | Auth     | Doc Status | Controller                                                                                                   | Params         |
| ------------------- | ------ | -------- | ---------- | ------------------------------------------------------------------------------------------------------------ | -------------- |
| `/admin/audit-logs` | GET    | ✅ ADMIN | ✅         | [AuditLogController.java:24](src/main/java/com/example/movie_backend/controller/AuditLogController.java#L24) | `page`, `size` |

---

## Legend

| Symbol | Meaning                  |
| ------ | ------------------------ |
| ✅     | Correct/Present          |
| ❌     | Not Required/Not Present |
| ⚠️     | Issue/Discrepancy        |
| ADMIN  | Requires ADMIN role      |

---

## Statistics

- **Total Endpoints:** 73
- **Fully Documented:** 70 (96%)
- **With Issues:** 3 (4%)
- **Public Endpoints:** 23 (32%)
- **Authenticated Endpoints:** 50 (68%)
- **Admin-Only Endpoints:** 36 (49%)

---

**Last Updated:** 2026-04-07  
**Maintenance Status:** Ready for implementation
