export interface ApiResponse<T> {
  code: number
  message?: string
  result: T
}

export interface PageResponse<T> {
  currentPage: number
  totalPages: number
  pageSize: number
  totalElements: number
  data: T[]
}

export type MovieType = 'SERIES' | 'MOVIE' | 'ONA'
export type MovieStatus = 'ONGOING' | 'COMPLETED' | 'UPCOMING'

export interface GenreResponse {
  id: string
  name: string
  slug?: string
  description?: string
}

export interface StudioResponse {
  id: string
  name: string
  description?: string
  logo?: string
}

export interface FranchiseResponse {
  id: string
  name: string
  description?: string
  poster?: string
}

export interface MovieResponse {
  id: string
  title: string
  originalTitle?: string
  description?: string
  poster?: string
  banner?: string
  trailerUrl?: string
  type: MovieType
  status: MovieStatus
  releaseYear?: number
  totalEpisodes?: number
  rating?: number
  views?: number
  studio?: StudioResponse
  franchise?: FranchiseResponse
  genres?: GenreResponse[]
  createdAt?: string
  updatedAt?: string
}

export type VideoQuality = 'P360' | 'P480' | 'P720' | 'P1080'
export type VideoType = 'HLS' | 'MP4' | 'EMBED'

export interface VideoSourceResponse {
  id: string
  url: string
  quality?: VideoQuality
  type?: VideoType
}

export interface SubtitleResponse {
  id: string
  url: string
  language?: string
}

export interface EpisodeResponse {
  id: string
  episodeNumber: number
  title?: string
  duration?: number
  videoSources?: VideoSourceResponse[]
  subtitles?: SubtitleResponse[]
}

export interface RoleResponse {
  id?: string
  name: string
  description?: string
}

export interface UserResponse {
  id: string
  username: string
  email?: string
  fullName?: string
  avatar?: string
  enabled?: boolean
  roles?: RoleResponse[]
  createdAt?: string
  updatedAt?: string
}

export interface CommentResponse {
  id: string
  content: string
  user?: UserResponse
  likes?: number
  createdAt?: string
  replies?: CommentResponse[]
}

export interface ReviewResponse {
  id: string
  rating?: number
  content?: string
  user?: UserResponse
  createdAt?: string
}

export interface WatchHistoryResponse {
  id: string
  movie?: MovieResponse
  episode?: EpisodeResponse
  progress?: number
  updatedAt?: string
}

export interface AuthenticationResponse {
  token: string
  authenticated: boolean
}
