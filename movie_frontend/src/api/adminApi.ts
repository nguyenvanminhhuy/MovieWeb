import { apiFetch } from './client'
import type { 
  MovieResponse, 
  PageResponse, 
  UserResponse, 
  CommentResponse,
  GenreResponse,
  StudioResponse,
  FranchiseResponse
} from './types'

// Movie Management
export function getAllMoviesAdmin(page = 1, size = 10) {
  return apiFetch<PageResponse<MovieResponse>>(`/movies?page=${page}&size=${size}`)
}

export function createMovie(data: Partial<MovieResponse>) {
  return apiFetch<MovieResponse>('/movies', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function updateMovie(id: string, data: Partial<MovieResponse>) {
  return apiFetch<MovieResponse>(`/movies/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  })
}

export function deleteMovie(id: string) {
  return apiFetch<null>(`/movies/${id}`, {
    method: 'DELETE'
  })
}

// User Management
export function getAllUsers(page = 1, size = 10) {
  return apiFetch<PageResponse<UserResponse>>(`/admin/users?page=${page}&size=${size}`)
}

export function updateUserStatus(userId: string, enabled: boolean) {
  return apiFetch<UserResponse>(`/admin/users/${userId}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ enabled })
  })
}

export function deleteUser(userId: string) {
  return apiFetch<null>(`/admin/users/${userId}`, {
    method: 'DELETE'
  })
}

// Comment Management
export function getAllCommentsAdmin(page = 1, size = 20) {
  return apiFetch<PageResponse<CommentResponse>>(`/admin/comments?page=${page}&size=${size}`)
}

export function deleteCommentAdmin(commentId: string) {
  return apiFetch<null>(`/admin/comments/${commentId}`, {
    method: 'DELETE'
  })
}

// Genre/Studio/Franchise Management (Helper for Movie creation)
export function getAllGenresAdmin() {
  return apiFetch<GenreResponse[]>('/admin/genres')
}

export function getAllStudiosAdmin() {
  return apiFetch<StudioResponse[]>('/admin/studios')
}

export function getAllFranchisesAdmin() {
  return apiFetch<FranchiseResponse[]>('/admin/franchises')
}
