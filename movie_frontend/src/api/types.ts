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

export type MovieType = 'TV_SERIES' | 'MOVIE' | 'OVA' | 'SPECIAL' | 'SERIES' | 'ONA'
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
  logoUrl?: string
}

export interface FranchiseResponse {
  id: string
  name: string
  description?: string
  poster?: string
  coverUrl?: string
}

export interface MovieResponse {
  id: string
  title: string
  originalTitle?: string
  description?: string
  poster?: string
  posterUrl?: string
  banner?: string
  bannerUrl?: string
  trailerUrl?: string
  type: MovieType
  status: MovieStatus
  releaseYear?: number
  releaseDate?: string
  totalEpisodes?: number
  episodeCount?: number
  rating?: number
  views?: number
  studio?: StudioResponse
  studios?: string[] | StudioResponse[]
  franchise?: FranchiseResponse
  genres?: GenreResponse[] | string[]
  createdAt?: string
  updatedAt?: string
}

export type VideoQuality = 'P360' | 'P480' | 'P720' | 'P1080'
export type VideoType = 'HLS' | 'MP4' | 'EMBED'

export interface VideoSourceResponse {
  id: string
  episodeId?: string
  url?: string
  videoUrl?: string
  quality?: VideoQuality | string
  type?: VideoType
  embedCode?: string
}

export interface SubtitleResponse {
  id: string
  episodeId?: string
  url?: string
  subtitleUrl?: string
  language?: string
}

export interface EpisodeResponse {
  id: string
  movieId?: string
  episodeNumber: number
  title?: string
  description?: string
  duration?: number
  thumbnailUrl?: string
  videoSources?: VideoSourceResponse[]
  subtitles?: SubtitleResponse[]
}

export interface RoleResponse {
  id?: string
  name: string
  description?: string
  permissions?: any[]
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
  movieId?: string
  userId?: string
  userName?: string
  content: string
  user?: UserResponse
  likes?: number
  createdAt?: string
  replies?: CommentResponse[]
}

export interface ReviewResponse {
  id: string
  movieId?: string
  userId?: string
  userName?: string
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

export interface ReportResponse {
  id: string
  user?: UserResponse
  movie?: MovieResponse
  episode?: EpisodeResponse
  reason?: string
  description?: string
  resolved?: boolean
  createdAt?: string
}

export interface AuditLogResponse {
  id: string
  username?: string
  action?: string
  details?: string
  ipAddress?: string
  createdAt?: string
}

export interface AuthenticationResponse {
  token: string
  refreshToken?: string
  authenticated: boolean
}
