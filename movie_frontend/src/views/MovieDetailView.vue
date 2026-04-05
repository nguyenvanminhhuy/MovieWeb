<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import * as api from '../api/animeApi'
import type { MovieResponse } from '../api/types'
import AppShell from '../components/AppShell.vue'
import MovieCard from '../components/MovieCard.vue'
import { mediaUrl } from '../utils/media'

const route = useRoute()
const movie = ref<MovieResponse | null>(null)
const related = ref<MovieResponse[]>([])
const loading = ref(true)
const err = ref('')

onMounted(async () => {
  const id = route.params.id as string
  loading.value = true
  err.value = ''
  try {
    const [m, rel] = await Promise.all([
      api.getMovie(id),
      api.getRelatedMovies(id, 1, 8),
    ])
    movie.value = m
    related.value = rel.data
    void api.incrementViews(id)
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không tìm thấy phim'
  } finally {
    loading.value = false
  }
})

function statusLabel(s: string) {
  const map: Record<string, string> = {
    ONGOING: 'Đang chiếu',
    COMPLETED: 'Hoàn thành',
    UPCOMING: 'Sắp chiếu',
  }
  return map[s] || s
}

function typeLabel(t: string) {
  const map: Record<string, string> = {
    SERIES: 'TV Series',
    MOVIE: 'Movie',
    ONA: 'ONA',
  }
  return map[t] || t
}
</script>

<template>
  <AppShell>
    <div v-if="loading" class="py-24 text-center text-zinc-500">Đang tải…</div>
    <div v-else-if="err || !movie" class="text-center text-red-400">{{ err || 'Không có dữ liệu' }}</div>
    <div v-else>
      <div class="relative mb-10 overflow-hidden rounded-2xl ring-1 ring-white/10">
        <div class="absolute inset-0">
          <img
            v-if="mediaUrl(movie.banner || movie.poster)"
            :src="mediaUrl(movie.banner || movie.poster)"
            alt=""
            class="h-full w-full object-cover opacity-40"
          />
          <div class="absolute inset-0 bg-gradient-to-t from-zinc-950 via-zinc-950/80 to-transparent" />
        </div>
        <div class="relative flex flex-col gap-6 px-6 py-10 md:flex-row md:px-10">
          <div class="w-48 shrink-0 overflow-hidden rounded-xl ring-1 ring-white/10">
            <img
              v-if="mediaUrl(movie.poster)"
              :src="mediaUrl(movie.poster)"
              :alt="movie.title"
              class="aspect-[2/3] w-full object-cover"
            />
            <div
              v-else
              class="flex aspect-[2/3] items-center justify-center bg-zinc-800 p-2 text-center text-sm text-zinc-500"
            >
              {{ movie.title }}
            </div>
          </div>
          <div class="min-w-0 flex-1">
            <h1 class="text-3xl font-bold md:text-4xl">{{ movie.title }}</h1>
            <p v-if="movie.originalTitle" class="mt-1 text-zinc-500">{{ movie.originalTitle }}</p>
            <div class="mt-4 flex flex-wrap gap-2">
              <span class="rounded-full bg-violet-600/30 px-3 py-1 text-xs font-medium text-violet-200">
                {{ typeLabel(movie.type) }}
              </span>
              <span class="rounded-full bg-white/10 px-3 py-1 text-xs">{{ statusLabel(movie.status) }}</span>
              <span v-if="movie.releaseYear" class="rounded-full bg-white/10 px-3 py-1 text-xs">{{
                movie.releaseYear
              }}</span>
              <span v-if="movie.rating" class="rounded-full bg-amber-500/20 px-3 py-1 text-xs text-amber-200"
                >★ {{ movie.rating.toFixed(1) }}</span
              >
              <span v-if="movie.views != null" class="rounded-full bg-white/10 px-3 py-1 text-xs"
                >{{ movie.views.toLocaleString('vi-VN') }} lượt xem</span
              >
            </div>
            <div v-if="movie.genres?.length" class="mt-3 flex flex-wrap gap-2">
              <span
                v-for="g in movie.genres"
                :key="g.id"
                class="rounded-lg bg-zinc-800 px-2 py-1 text-xs text-zinc-300"
                >{{ g.name }}</span
              >
            </div>
            <p class="mt-6 whitespace-pre-wrap text-zinc-300 leading-relaxed">
              {{ movie.description || 'Chưa có mô tả.' }}
            </p>
            <div class="mt-8 flex flex-wrap gap-3">
              <RouterLink
                :to="{ name: 'watch', params: { movieId: movie.id } }"
                class="inline-flex rounded-xl bg-violet-600 px-8 py-3 font-semibold hover:bg-violet-500"
              >
                Xem phim
              </RouterLink>
              <a
                v-if="movie.trailerUrl"
                :href="movie.trailerUrl"
                target="_blank"
                rel="noopener"
                class="inline-flex rounded-xl border border-white/20 px-8 py-3 font-medium hover:bg-white/10"
              >
                Trailer
              </a>
            </div>
            <div v-if="movie.studio" class="mt-6 text-sm text-zinc-500">
              Studio:
              <span class="text-zinc-300">{{ movie.studio.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <section v-if="related.length">
        <h2 class="mb-4 text-xl font-bold">Có thể bạn thích</h2>
        <div class="grid grid-cols-2 gap-4 sm:grid-cols-4 lg:grid-cols-6">
          <MovieCard v-for="m in related" :key="m.id" :movie="m" />
        </div>
      </section>
    </div>
  </AppShell>
</template>
