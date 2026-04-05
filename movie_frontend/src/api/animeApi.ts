import { apiFetch } from './client'
import type {
  AuthenticationResponse,
  CommentResponse,
  EpisodeResponse,
  MovieResponse,
  PageResponse,
  ReviewResponse,
  UserResponse,
  VideoSourceResponse,
  SubtitleResponse,
  WatchHistoryResponse,
} from './types'
import type { MovieStatus, MovieType } from './types'

export function login(username: string, password: string) {
  return apiFetch<AuthenticationResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
}

export function logout() {
  const token = localStorage.getItem('access_token')
  return apiFetch<null>('/auth/logout', {
    method: 'POST',
    body: JSON.stringify({ token }),
  })
}

export function introspectToken(token: string) {
  return apiFetch<{ valid: boolean }>('/auth/introspect', {
    method: 'POST',
    body: JSON.stringify({ token }),
  })
}

export function refreshToken(token: string) {
  return apiFetch<AuthenticationResponse>('/auth/refresh', {
    method: 'POST',
    body: JSON.stringify({ token }),
  })
}

export function getMovies(page = 1, size = 12) {
  return apiFetch<PageResponse<MovieResponse>>(`/common/movies?page=${page}&size=${size}`)
}

export function getTopMovies(type: 'day' | 'week' | 'month' | 'all' = 'all', page = 1, size = 12) {
  return apiFetch<PageResponse<MovieResponse>>(
    `/common/movies/top?type=${encodeURIComponent(type)}&page=${page}&size=${size}`,
  )
}

export function searchMovies(params: {
  query?: string
  genreId?: string
  franchiseId?: string
  type?: MovieType
  status?: MovieStatus
  page?: number
  size?: number
}) {
  const q = new URLSearchParams()
  if (params.query) q.set('query', params.query)
  if (params.genreId) q.set('genreId', params.genreId)
  if (params.franchiseId) q.set('franchiseId', params.franchiseId)
  if (params.type) q.set('type', params.type)
  if (params.status) q.set('status', params.status)
  q.set('page', String(params.page ?? 1))
  q.set('size', String(params.size ?? 12))
  return apiFetch<PageResponse<MovieResponse>>(`/common/movies/search?${q.toString()}`)
}

export function getMovie(id: string) {
  return apiFetch<MovieResponse>(`/common/movies/${encodeURIComponent(id)}`)
}

export function getRelatedMovies(id: string, page = 1, size = 8) {
  return apiFetch<PageResponse<MovieResponse>>(
    `/common/movies/${encodeURIComponent(id)}/related?page=${page}&size=${size}`,
  )
}

export function incrementViews(movieId: string) {
  return apiFetch<null>(`/common/movies/${encodeURIComponent(movieId)}/views`, { method: 'POST' })
}

export function getGenres(page = 1, size = 100) {
  return apiFetch<PageResponse<{ id: string; name: string; slug?: string; description?: string }>>(
    `/common/genres?page=${page}&size=${size}`,
  )
}

export function getEpisodes(movieId: string) {
  return apiFetch<EpisodeResponse[]>(`/common/episodes/movie/${encodeURIComponent(movieId)}`)
}

export function getVideoSources(episodeId: string) {
  return apiFetch<VideoSourceResponse[]>(
    `/common/video-sources/episode/${encodeURIComponent(episodeId)}`,
  )
}

export function getSubtitles(episodeId: string) {
  return apiFetch<SubtitleResponse[]>(`/common/subtitles/episode/${encodeURIComponent(episodeId)}`)
}

export function getComments(movieId: string) {
  return apiFetch<CommentResponse[]>(`/common/comments/movie/${encodeURIComponent(movieId)}`)
}

export function postComment(movieId: string, content: string, parentId?: string) {
  return apiFetch<CommentResponse>('/comments', {
    method: 'POST',
    body: JSON.stringify({ movieId, content, parentId }),
  })
}

export function likeComment(commentId: string) {
  return apiFetch<CommentResponse>(`/comments/${encodeURIComponent(commentId)}/like`, {
    method: 'POST',
  })
}

export function getReviews(movieId: string) {
  return apiFetch<ReviewResponse[]>(`/common/reviews/movie/${encodeURIComponent(movieId)}`)
}

export function postReview(movieId: string, rating: number, content: string) {
  return apiFetch<ReviewResponse>('/reviews', {
    method: 'POST',
    body: JSON.stringify({ movieId, rating, content }),
  })
}

export function getMyInfo() {
  return apiFetch<UserResponse>('/users/my-info')
}

export function updateProfile(body: {
  email?: string
  fullName?: string
  avatar?: string
}) {
  return apiFetch<UserResponse>('/users/update-profile', {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

export function registerUser(body: {
  username: string
  password: string
  email: string
  fullName?: string
}) {
  return apiFetch<UserResponse>('/users', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export function addFavorite(movieId: string) {
  return apiFetch<null>(`/favorites/${encodeURIComponent(movieId)}`, { method: 'POST' })
}

export function removeFavorite(movieId: string) {
  return apiFetch<null>(`/favorites/${encodeURIComponent(movieId)}`, { method: 'DELETE' })
}

export function getFavorites(page = 1, size = 12) {
  return apiFetch<PageResponse<MovieResponse>>(`/favorites?page=${page}&size=${size}`)
}

export function saveWatchProgress(episodeId: string, watchedDuration: number, totalDuration: number) {
  return apiFetch<null>('/history', {
    method: 'POST',
    body: JSON.stringify({ episodeId, watchedDuration, totalDuration }),
  })
}

export function getHistory(page = 1, size = 12) {
  return apiFetch<PageResponse<WatchHistoryResponse>>(`/history?page=${page}&size=${size}`)
}
