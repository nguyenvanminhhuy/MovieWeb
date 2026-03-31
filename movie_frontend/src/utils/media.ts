/**
 * Resolve poster/banner/avatar URLs. Backend serves uploads at /uploads/** — proxied in Vite dev.
 */
export function mediaUrl(path: string | null | undefined): string {
  if (!path) return ''
  if (/^https?:\/\//i.test(path)) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path : `/${path}`
  if (normalized.startsWith('/uploads')) return normalized
  return normalized
}
