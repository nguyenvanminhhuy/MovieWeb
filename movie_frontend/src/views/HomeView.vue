<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import * as api from '../api/animeApi'
import type { MovieResponse } from '../api/types'
import AppShell from '../components/AppShell.vue'
import MovieCard from '../components/MovieCard.vue'
import { mediaUrl } from '../utils/media'

const latest = ref<MovieResponse[]>([])
const trending = ref<MovieResponse[]>([])
const hero = ref<MovieResponse | null>(null)
const loading = ref(true)
const err = ref('')

onMounted(async () => {
  loading.value = true
  err.value = ''
  try {
    const [all, top] = await Promise.all([
      api.getMovies(1, 12),
      api.getTopMovies('week', 1, 8),
    ])
    latest.value = all.data
    trending.value = top.data
    hero.value = top.data[0] || all.data[0] || null
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không tải được dữ liệu'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <AppShell>
    <div v-if="loading" class="py-24 text-center text-zinc-500">Đang tải…</div>
    <div v-else-if="err" class="rounded-xl bg-red-950/50 p-6 text-red-300 ring-1 ring-red-500/30">
      {{ err }}
    </div>
    <div v-else class="space-y-12">
      <section
        v-if="hero"
        class="relative overflow-hidden rounded-2xl ring-1 ring-white/10"
      >
        <div class="absolute inset-0">
          <img
            v-if="mediaUrl(hero.banner || hero.poster)"
            :src="mediaUrl(hero.banner || hero.poster)"
            alt=""
            class="h-full w-full object-cover opacity-50"
          />
          <div class="absolute inset-0 bg-gradient-to-r from-zinc-950 via-zinc-950/90 to-transparent" />
        </div>
        <div class="relative flex flex-col gap-4 px-6 py-16 md:flex-row md:items-end md:px-12 md:py-20">
          <div class="max-w-xl">
            <p class="text-sm font-medium uppercase tracking-widest text-violet-400">Nổi bật tuần này</p>
            <h1 class="mt-2 text-3xl font-bold tracking-tight md:text-5xl">{{ hero.title }}</h1>
            <p class="mt-3 line-clamp-3 text-zinc-400">{{ hero.description || 'Không có mô tả.' }}</p>
            <div class="mt-6 flex flex-wrap gap-3">
              <RouterLink
                :to="{ name: 'watch', params: { movieId: hero.id } }"
                class="inline-flex items-center rounded-xl bg-violet-600 px-6 py-3 font-semibold hover:bg-violet-500"
              >
                Xem ngay
              </RouterLink>
              <RouterLink
                :to="{ name: 'movie', params: { id: hero.id } }"
                class="inline-flex items-center rounded-xl border border-white/20 px-6 py-3 font-medium hover:bg-white/10"
              >
                Chi tiết
              </RouterLink>
            </div>
          </div>
        </div>
      </section>

      <section>
        <div class="mb-4 flex items-end justify-between">
          <h2 class="text-xl font-bold">Xu hướng</h2>
          <RouterLink to="/browse" class="text-sm text-violet-400 hover:underline">Xem tất cả</RouterLink>
        </div>
        <div class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6">
          <MovieCard v-for="m in trending" :key="m.id" :movie="m" />
        </div>
      </section>

      <section>
        <div class="mb-4 flex items-end justify-between">
          <h2 class="text-xl font-bold">Mới cập nhật</h2>
        </div>
        <div class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6">
          <MovieCard v-for="m in latest" :key="m.id" :movie="m" />
        </div>
      </section>
    </div>
  </AppShell>
</template>
