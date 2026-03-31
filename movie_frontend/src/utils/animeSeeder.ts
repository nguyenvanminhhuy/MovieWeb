import * as adminApi from '../api/adminApi'

/**
 * Kiểm tra xem Backend có đang hoạt động hay không
 */
export async function checkBackendStatus(): Promise<boolean> {
  try {
    const response = await fetch('/api/movies?page=1&size=1', { method: 'GET' })
    // Nếu status là 200...499 thì coi như server có phản hồi
    return response.status < 500
  } catch (err) {
    console.error('[Seeder] Backend connection failed:', err)
    return false
  }
}

/**
 * Lấy dữ liệu Anime từ Jikan API và nạp vào Backend của bạn
 * @param limit Số lượng cần nạp (1-25)
 */
export async function seedTopAnime(limit = 10) {
  try {
    // 1. Kiểm tra Backend trước
    const isOnline = await checkBackendStatus()
    if (!isOnline) {
      throw new Error(
        'Backend chưa được khởi động (Cổng 8080). Hãy bật Server Java lên trước khi nạp dữ liệu.',
      )
    }

    // 2. Lấy data từ Jikan
    console.log(`[Seeder] Đang lấy ${limit} Anime từ Jikan API...`)
    const response = await fetch(`https://api.jikan.moe/v4/top/anime?limit=${limit}`)

    if (!response.ok) {
      throw new Error(`Jikan API Error: ${response.status} ${response.statusText}`)
    }

    const { data } = await response.json()
    if (!data || !Array.isArray(data)) {
      throw new Error('Dữ liệu từ Jikan không hợp lệ.')
    }

    let successCount = 0
    let firstError: any = null

    for (const anime of data) {
      const payload: any = {
        title: anime.title,
        originalTitle: anime.title_japanese,
        description: anime.synopsis?.substring(0, 200) + '...',
        poster: anime.images?.webp?.large_image_url || anime.images?.jpg?.large_image_url,
        banner: anime.images?.webp?.large_image_url || anime.images?.jpg?.large_image_url,
        type: mapType(anime.type),
        status: mapStatus(anime.status),
        releaseYear:
          anime.year || (anime.aired?.from ? new Date(anime.aired.from).getFullYear() : undefined),
        totalEpisodes: anime.episodes,
        rating: anime.score,
        // Thêm các mảng trống để tránh lỗi "Null" ở Backend nếu cần
        genres: [],
        studios: [],
        franchises: [],
      }

      try {
        await adminApi.createMovie(payload)
        successCount++
        console.log(`[Seeder] Đã nạp: ${anime.title}`)
      } catch (err) {
        if (!firstError) firstError = err
        console.warn(`[Seeder] Bỏ qua ${anime.title} do lỗi backend:`, err)
      }

      // Nghỉ 500ms để tránh bị rate-limit bởi Jikan
      await new Promise((r) => setTimeout(r, 500))
    }

    if (successCount === 0 && firstError) {
      if (firstError.name === 'ApiError') {
        const details = `Mã lỗi: ${firstError.code} - ${firstError.message}`
        throw new Error(
          `Dữ liệu bị Backend từ chối!\nLý do: ${details}\n\nGợi ý: Kiểm tra xem tài khoản của bạn đã được gắn vai trò ADMIN trong database chưa?`,
        )
      }
      throw firstError
    }

    return successCount
  } catch (error) {
    console.error('[Seeder] Fatal error:', error)
    throw error
  }
}

function mapType(t: string): string {
  const typeMap: Record<string, string> = {
    TV: 'SERIES',
    Movie: 'MOVIE',
    ONA: 'ONA',
    OVA: 'SERIES',
    Special: 'SERIES',
  }
  return typeMap[t] || 'SERIES'
}

function mapStatus(s: string): string {
  if (s === 'Finished Airing') return 'COMPLETED'
  if (s === 'Currently Airing') return 'ONGOING'
  return 'UPCOMING'
}

/**
 * Nạp Anime từ Kitsu (Nguồn Anime khác, không cần Key)
 */
export async function seedFromKitsu(limit = 10) {
  try {
    const response = await fetch(`https://kitsu.io/api/edge/anime?page[limit]=${limit}`)
    const result = await response.json()
    if (!result.data) throw new Error('Không thể lấy dữ liệu từ Kitsu')

    let count = 0
    for (const anime of result.data) {
      const { attributes } = anime
      const payload: any = {
        title: attributes.canonicalTitle,
        originalTitle: attributes.titles?.ja_jp || '',
        description: attributes.synopsis?.substring(0, 200) + '...',
        poster: attributes.posterImage?.large || attributes.posterImage?.original,
        banner: attributes.coverImage?.large || attributes.posterImage?.large,
        type: 'SERIES',
        status: attributes.status === 'finished' ? 'COMPLETED' : 'ONGOING',
        releaseYear: attributes.startDate
          ? new Date(attributes.startDate).getFullYear()
          : undefined,
        totalEpisodes: attributes.episodeCount,
        rating: parseFloat(attributes.averageRating) / 10 || 0,
        genres: [],
        studios: [],
        franchises: [],
      }
      try {
        await adminApi.createMovie(payload)
        count++
        console.log(`[Kitsu] Đã nạp: ${attributes.canonicalTitle}`)
      } catch (err) {
        console.warn(err)
      }
      await new Promise((r) => setTimeout(r, 400))
    }
    return count
  } catch (error) {
    throw error
  }
}

/**
 * Nạp Phim từ TMDb (Nguồn phim điện ảnh lớn nhất, cần API KEY)
 */
export async function seedFromTMDb(limit = 10, apiKey: string) {
  try {
    const response = await fetch(
      `https://api.themoviedb.org/3/movie/popular?api_key=${apiKey}&language=vi-VN&page=1`,
    )
    const result = await response.json()
    if (!result.results) throw new Error('API Key TMDb không hợp lệ hoặc lỗi kết nối')

    let count = 0
    const movies = result.results.slice(0, limit)
    for (const movie of movies) {
      const payload: any = {
        title: movie.title,
        originalTitle: movie.original_title,
        description: movie.overview?.substring(0, 200) + '...',
        poster: movie.poster_path ? `https://image.tmdb.org/t/p/w500${movie.poster_path}` : '',
        banner: movie.backdrop_path
          ? `https://image.tmdb.org/t/p/original${movie.backdrop_path}`
          : '',
        type: 'MOVIE',
        status: 'COMPLETED',
        releaseYear: movie.release_date ? new Date(movie.release_date).getFullYear() : undefined,
        totalEpisodes: 1,
        rating: movie.vote_average,
        genres: [],
        studios: [],
        franchises: [],
      }
      try {
        await adminApi.createMovie(payload)
        count++
        console.log(`[TMDb] Đã nạp: ${movie.title}`)
      } catch (err) {
        console.warn(err)
      }
      await new Promise((r) => setTimeout(r, 400))
    }
    return count
  } catch (error) {
    throw error
  }
}

/**
 * Nạp Phim từ TVMaze (Nguồn phim truyền hình phương Tây khổng lồ, KHÔNG cần Key)
 */
export async function seedFromTVMaze(limit = 10, page = 0) {
  try {
    const response = await fetch(`https://api.tvmaze.com/shows?page=${page}`)
    const shows = await response.json()
    if (!Array.isArray(shows)) throw new Error('Không thể kết nối tới TVMaze')

    let count = 0
    const selection = shows.slice(0, limit)

    for (const show of selection) {
      const payload: any = {
        title: show.name,
        originalTitle: show.name,
        description: stripHtml(show.summary)?.substring(0, 200) + '...',
        poster: show.image?.original || show.image?.medium || '',
        banner: show.image?.original || '',
        type: 'SERIES',
        status: show.status === 'Ended' ? 'COMPLETED' : 'ONGOING',
        releaseYear: show.premiered ? new Date(show.premiered).getFullYear() : undefined,
        totalEpisodes: 1,
        rating: show.rating?.average || 0,
        genres: [],
        studios: [],
        franchises: [],
      }
      try {
        await adminApi.createMovie(payload)
        count++
        console.log(`[TVMaze] Đã nạp: ${show.name}`)
      } catch (err) {
        console.warn(err)
      }
      await new Promise((r) => setTimeout(r, 400))
    }
    return count
  } catch (error) {
    throw error
  }
}

function stripHtml(html: string): string {
  if (!html) return ''
  return html.replace(/<[^>]*>?/gm, '')
}

function slugify(text: string): string {
  return text
    .toString()
    .toLowerCase()
    .normalize('NFD') // Chuẩn hóa Unicode
    .replace(/[\u0300-\u036f]/g, '') // Loại bỏ các dấu tiếng Việt
    .replace(/\s+/g, '-') // Thay khoảng trắng bằng dấu gạch ngang
    .replace(/[^\w-]+/g, '') // Loại bỏ các ký tự đặc biệt
    .replace(/--+/g, '-') // Loại bỏ các gạch ngang dư thừa
    .replace(/^-+/, '') // Loại bỏ gạch ngang ở đầu
    .replace(/-+$/, '') // Loại bỏ gạch ngang ở cuối
}

/**
 * Nạp Anime từ AniList (Nguồn Anime mạnh mẽ nhất - GraphQL)
 */
export async function seedFromAniList(limit = 10, page = 1) {
  const query = `
    query ($page: Int, $perPage: Int) {
      Page (page: $page, perPage: $perPage) {
        media (type: ANIME, sort: POPULARITY_DESC) {
          title { romaji english native }
          description
          bannerImage
          coverImage { extraLarge large }
          status
          episodes
          seasonYear
          averageScore
        }
      }
    }
  `

  try {
    const response = await fetch('https://graphql.anilist.co', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query, variables: { page, perPage: limit } }),
    })

    const { data } = await response.json()
    if (!data?.Page?.media) throw new Error('Không thể lấy dữ liệu từ AniList')

    let count = 0
    let firstError: any = null

    for (const anime of data.Page.media) {
      const title = anime.title.english || anime.title.romaji || 'Untitled'
      const payload: any = {
        title: title.substring(0, 100),
        slug: slugify(title) + '-' + Math.floor(Math.random() * 1000),
        originalTitle: (anime.title.native || '')?.substring(0, 100),
        description: stripHtml(anime.description)?.substring(0, 150) + '...',
        poster: anime.coverImage?.extraLarge || anime.coverImage?.large,
        banner: anime.bannerImage || anime.coverImage?.extraLarge,
        type: 'MOVIE',
        status: anime.status === 'FINISHED' ? 'COMPLETED' : 'ONGOING',
        releaseYear: anime.seasonYear,
        totalEpisodes: anime.episodes || 0,
        rating: anime.averageScore / 10 || 0,
        views: 0,
      }
      try {
        await adminApi.createMovie(payload)
        count++
        console.log(`[AniList] Đã nạp thành công: ${payload.title}`)
      } catch (err: any) {
        firstError = err
        const msg = err.message || ''
        if (msg.includes('duplicate') || msg.includes('exists') || err.code === 409) {
          console.warn(`[AniList] Bỏ qua "${payload.title}" vì đã tồn tại trong hệ thống.`)
        } else {
          console.warn(`[AniList] Lỗi khi nạp "${payload.title}":`, err)
        }
      }
      await new Promise((r) => setTimeout(r, 450))
    }
    return count
  } catch (error) {
    throw error
  }
}
