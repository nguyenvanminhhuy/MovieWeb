import type { ApiResponse } from './types'

const API_PREFIX = import.meta.env.VITE_API_PREFIX ?? '/api'

export class ApiError extends Error {
  constructor(
    public readonly code: number,
    message: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

function getToken(): string | null {
  return localStorage.getItem('access_token')
}

export async function apiFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers)
  const token = getToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)

  const hasBody = init.body !== undefined && init.body !== null
  if (hasBody && typeof init.body === 'string' && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const res = await fetch(`${API_PREFIX}${path}`, { ...init, headers })
  let json: ApiResponse<T> | { code: number; message?: string } | null = null
  try {
    json = await res.json()
  } catch {
    throw new ApiError(res.status, res.statusText || 'Invalid response')
  }

  const code = (json as { code?: number }).code
  const message = (json as { message?: string }).message ?? 'Request failed'

  if (!res.ok || (code !== undefined && code !== 1000)) {
    throw new ApiError(code ?? res.status, message)
  }

  return (json as ApiResponse<T>).result
}
