# 🔍 Admin API Audit Report & Required Fixes

**Date:** 2026-04-23
**Status:** Review Completed
**Target:** Backend Team (Java Spring Boot)

## 1. Admin Dashboard Statistics Discrepancy
**Endpoint:** `GET /admin/stats/dashboard`

### 🚩 Issues:
*   **Missing Fields:** The frontend dashboard requires several metrics that are currently missing from the backend response or inconsistent with documentation.
*   **Current Requirement:** The frontend expects the following structure:
    ```json
    {
      "totalUsers": 0,
      "totalMovies": 0,
      "totalEpisodes": 0,
      "totalViews": 0,
      "activeUsers": 0,
      "newUsersThisMonth": 0,
      "totalComments": 0,
      "totalReviews": 0,
      "totalReports": 0,
      "unresolvedReports": 0,
      "totalGenres": 0,
      "totalStudios": 0,
      "totalFranchises": 0
    }
    ```
*   **Root Cause:** The backend documentation (version 1.1) only specifies `totalMovies`, `totalUsers`, `totalViews`, `totalComments`, `moviesByType`, and `moviesByStatus`.

### ✅ Required Fix:
Expand the `StatisticsResponse` or the result of `/admin/stats/dashboard` to include all the missing counts listed above to avoid multiple API calls from the dashboard.

---

## 2. Movie Management Data Inconsistency
**Endpoints:** `POST /movies`, `PUT /movies/{id}`, `GET /movies`

### 🚩 Issues:
*   **Naming Mismatch:** There is a conflict between documented fields and what the frontend was initially receiving/sending.
*   **Specific mismatches:**
    *   **Poster:** `poster` vs `posterUrl`
    *   **Banner:** `banner` vs `bannerUrl`
    *   **Date:** `releaseYear` (Integer) vs `releaseDate` (String YYYY-MM-DD)
    *   **Associations:** `studio` (Object/String ID) vs `studios` (Array of IDs)

### ✅ Required Fix:
Standardize the `MovieDTO` and `MovieResponse` to follow the version 1.1 Documentation:
*   Use `posterUrl` and `bannerUrl` consistently.
*   Use `releaseDate` (ISO Date string) instead of just year.
*   Support `studios` as an array of IDs for both creation and update.
*   **CRITICAL BUG:** The `PUT /movies/{id}` endpoint fails to update the `studio` relationship if the movie already has a studio. It seems the backend `updateMovie` logic is ignoring the `studio` / `studioId` field during updates if it's already set.

---

## 3. User Management Endpoint Mismatch
**Endpoint:** `PUT /users/{userId}`

### 🚩 Issues:
*   **Field Mismatch:** The frontend implementation for "Edit User" sends `username`, `email`, and `roles` (array of objects with `name`).
*   **Documentation Mismatch:** Version 1.1 documentation states this endpoint only accepts `email`, `fullName`, and `avatar`.
*   **Problem:** Admin cannot currently update roles or usernames through the standard update endpoint if the documentation is correct.

### ✅ Required Fix:
Update the `UserUpdateDTO` on the backend to allow `roles` and `username` updates (restricted to ADMIN role), or confirm if a separate endpoint (e.g., `/users/{id}/roles`) should be used.

---

## 4. Media Upload Configuration
**Endpoint:** `POST /media/upload`

### 🚩 Issues:
*   **Base URL Hardcoding:** The frontend has a hardcoded fallback to `http://localhost:8080/api/v1/media/upload`.
*   **Proxy Interaction:** Files are uploaded via `FormData`, but the fetch call in `adminApi.ts` doesn't use the standard `apiFetch` wrapper, potentially causing issues with the `/api` target rewrite in `vite.config.ts`.

### ✅ Required Fix:
Ensure the backend supports multipart uploads at `/api/v1/media/upload` (relative to base) and returns the full URL (or a predictable path) in the `result` field.

---

## 5. Response Code Synchronization
**Context:** Global Response Wrapper

### 🚩 Findings:
*   The frontend expects success code `1000`.
*   Ensure all Error Responses (4xx, 5xx) still follow the `ApiResponse` format with a clear `message` and the corresponding `code`, so the frontend can display meaningful alerts instead of generic "500 Internal Server Error".

---

## 6. Episode Count Missing in Movie List
**Endpoint:** GET /movies or GET /movies/page 

### ?? Issues:
*   **Missing Data:** When the frontend fetches the list of movies, the backend does not return the number of episodes (episodeCount) that actually exist for each movie. 
*   **Problem:** The admin UI table shows '0 t?p' for all movies because the frontend cannot know how many episodes have been added without making N separate API calls to /episodes.

### ? Required Fix:
Update the MovieResponse / MovieDTO in Java Spring Boot to include episodeCount (an integer). The backend should calculate this by returning the size of the episodes list associated with the movie (movie.getEpisodes().size()).
