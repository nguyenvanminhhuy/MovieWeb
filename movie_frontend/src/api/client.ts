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
  
  if (res.status === 204) {
    return null as any
  }

  let json: any = null
  const contentType = res.headers.get('Content-Type')
  if (contentType && contentType.includes('application/json')) {
    try {
      json = await res.json()
    } catch {
      // Ignore parse error if status is OK
      if (!res.ok) throw new ApiError(res.status, 'Invalid JSON response')
    }
  }

  if (!res.ok) {
    const message = json?.message || res.statusText || 'Request failed'
    const code = json?.code || res.status
    throw new ApiError(code, message)
  }

  return json?.result ?? json
}
