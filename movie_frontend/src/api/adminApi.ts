import { apiFetch } from './client'
import type {
  MovieResponse,
  PageResponse,
  UserResponse,
  CommentResponse,
  GenreResponse,
  StudioResponse,
  FranchiseResponse,
  EpisodeResponse,
  ReviewResponse,
  VideoSourceResponse,
  SubtitleResponse,
} from './types'

// Movie Management
export function getAllMoviesAdmin(page = 1, size = 10) {
  return apiFetch<PageResponse<MovieResponse>>(`/movies?page=${page}&size=${size}`)
}

export function createMovie(data: Partial<MovieResponse>) {
  return apiFetch<MovieResponse>('/movies', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function updateMovie(id: string, data: Partial<MovieResponse>) {
  return apiFetch<MovieResponse>(`/movies/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function deleteMovie(id: string) {
  return apiFetch<null>(`/movies/${id}`, {
    method: 'DELETE',
  })
}

// User Management
export function getAllUsers(page = 1, size = 10) {
  return apiFetch<PageResponse<UserResponse>>(`/users?page=${page}&size=${size}`)
}

export function updateUserStatus(userId: string, enabled: boolean) {
  return apiFetch<UserResponse>(`/users/${userId}/status?enabled=${enabled}`, {
    method: 'PATCH',
  })
}

export function updateUser(id: string, data: Partial<UserResponse>) {
  return apiFetch<UserResponse>(`/users/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function deleteUser(userId: string) {
  return apiFetch<null>(`/users/${userId}`, {
    method: 'DELETE',
  })
}

// Comment Management
export function getAllCommentsAdmin(page = 1, size = 20) {
  return apiFetch<PageResponse<CommentResponse>>(`/comments?page=${page}&size=${size}`)
}

export function deleteCommentAdmin(commentId: string) {
  return apiFetch<null>(`/comments/${commentId}`, {
    method: 'DELETE',
  })
}

// Genre Management
export function getAllGenresAdmin(page = 1, size = 10) {
  return apiFetch<PageResponse<GenreResponse>>(`/genres?page=${page}&size=${size}`)
}

export function createGenre(data: Partial<GenreResponse>) {
  return apiFetch<GenreResponse>('/genres', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function updateGenre(id: string, data: Partial<GenreResponse>) {
  return apiFetch<GenreResponse>(`/genres/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function deleteGenre(id: string) {
  return apiFetch<null>(`/genres/${id}`, {
    method: 'DELETE',
  })
}

// Studio Management
export function getAllStudiosAdmin(page = 1, size = 10) {
  return apiFetch<PageResponse<StudioResponse>>(`/studios?page=${page}&size=${size}`)
}

export function createStudio(data: Partial<StudioResponse>) {
  return apiFetch<StudioResponse>('/studios', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function updateStudio(id: string, data: Partial<StudioResponse>) {
  return apiFetch<StudioResponse>(`/studios/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function deleteStudio(id: string) {
  return apiFetch<null>(`/studios/${id}`, {
    method: 'DELETE',
  })
}

// Franchise Management
export function getAllFranchisesAdmin(page = 1, size = 10) {
  return apiFetch<PageResponse<FranchiseResponse>>(`/franchises?page=${page}&size=${size}`)
}

export function createFranchise(data: Partial<FranchiseResponse>) {
  return apiFetch<FranchiseResponse>('/franchises', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function updateFranchise(id: string, data: Partial<FranchiseResponse>) {
  return apiFetch<FranchiseResponse>(`/franchises/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function deleteFranchise(id: string) {
  return apiFetch<null>(`/franchises/${id}`, {
    method: 'DELETE',
  })
}

// Episode Management
export function getEpisodesByMovieAdmin(movieId: string) {
  return apiFetch<EpisodeResponse[]>(`/episodes/movie/${movieId}`)
}

export function createEpisode(data: Partial<EpisodeResponse>) {
  return apiFetch<EpisodeResponse>('/episodes', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function updateEpisode(id: string, data: Partial<EpisodeResponse>) {
  return apiFetch<EpisodeResponse>(`/episodes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function deleteEpisode(id: string) {
  return apiFetch<null>(`/episodes/${id}`, {
    method: 'DELETE',
  })
}

// Video Source Management
export function getVideoSourcesByEpisodeAdmin(episodeId: string) {
  return apiFetch<VideoSourceResponse[]>(`/video-sources/episode/${episodeId}`)
}

export function createVideoSource(data: Partial<VideoSourceResponse>) {
  return apiFetch<VideoSourceResponse>('/video-sources', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function deleteVideoSource(id: string) {
  return apiFetch<null>(`/video-sources/${id}`, {
    method: 'DELETE',
  })
}

// Subtitle Management
export function getSubtitlesByEpisodeAdmin(episodeId: string) {
  return apiFetch<SubtitleResponse[]>(`/subtitles/episode/${episodeId}`)
}

export function createSubtitle(data: Partial<SubtitleResponse>) {
  return apiFetch<SubtitleResponse>('/subtitles', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function deleteSubtitle(id: string) {
  return apiFetch<null>(`/subtitles/${id}`, {
    method: 'DELETE',
  })
}

// Review Management (Distinct from comments)
export function getAllReviewsAdmin(page = 1, size = 20) {
  return apiFetch<PageResponse<ReviewResponse>>(`/reviews?page=${page}&size=${size}`)
}

export function deleteReviewAdmin(reviewId: string) {
  return apiFetch<null>(`/reviews/${reviewId}`, {
    method: 'DELETE',
  })
}

// Statistics
export function getAdminStats() {
  return apiFetch<{
    totalUsers: number
    totalMovies: number
    totalEpisodes: number
    totalViews: number
    activeUsers: number
    newUsersThisMonth: number
    totalComments: number
    totalReviews: number
    totalReports: number
    unresolvedReports: number
    totalGenres?: number
    totalStudios?: number
    totalFranchises?: number
  }>('/admin/stats/dashboard')
}

// Audit Logs
export function getAuditLogs(page = 1, size = 50) {
  return apiFetch<PageResponse<any>>(`/admin/audit-logs?page=${page}&size=${size}`)
}

// Media Upload
export async function uploadMedia(file: File, folder = 'general') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)

  const token = localStorage.getItem('access_token')
  const res = await fetch(
    `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'}/media/upload`,
    {
      method: 'POST',
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: formData,
    },
  )

  if (!res.ok) throw new Error('Upload failed')
  const data = await res.json()
  return data.result as string
}

// Report Management
export function getAllReportsAdmin(page = 1, size = 20) {
  return apiFetch<PageResponse<any>>(`/reports?page=${page}&size=${size}`)
}

export function resolveReport(id: string) {
  return apiFetch<null>(`/reports/${id}/resolve`, {
    method: 'PATCH',
  })
}

// Role Management
export function getAllRoles() {
  return apiFetch<any[]>('/roles')
}

export function createRole(data: any) {
  return apiFetch<any>('/roles', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function deleteRole(name: string) {
  return apiFetch<null>(`/roles/${name}`, {
    method: 'DELETE',
  })
}

// Permission Management
export function getAllPermissions() {
  return apiFetch<any[]>('/permissions')
}

export function createPermission(data: any) {
  return apiFetch<any>('/permissions', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function deletePermission(name: string) {
  return apiFetch<null>(`/permissions/${name}`, {
    method: 'DELETE',
  })
}
