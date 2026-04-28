<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import * as api from '../api/animeApi'
import type { MovieResponse } from '../api/types'
import type { MovieStatus, MovieType } from '../api/types'
import AppShell from '../components/AppShell.vue'
import MovieCard from '../components/MovieCard.vue'

const q = ref('')
const genreId = ref('')
const type = ref<MovieType | ''>('')
const status = ref<MovieStatus | ''>('')
const page = ref(1)
const totalPages = ref(1)
const movies = ref<MovieResponse[]>([])
const genres = ref<{ id: string; name: string }[]>([])
const loading = ref(false)
const err = ref('')

async function load() {
  loading.value = true
  err.value = ''
  try {
    const res = await api.getMovies(page.value, 18, {
      query: q.value || undefined,
      genreId: genreId.value || undefined,
      type: type.value || undefined,
      status: status.value || undefined,
    })
    movies.value = res.data
    totalPages.value = res.totalPages
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Lỗi tải danh sách'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const g = await api.getGenres(1, 200)
    genres.value = g.data.map((x) => ({ id: x.id, name: x.name }))
  } catch {
    /* ignore */
  }
  await load()
})

watch([q, genreId, type, status], () => {
  page.value = 1
  void load()
})

function goPage(p: number) {
  page.value = p
  void load()
}
</script>

<template>
  <AppShell>
    <div class="mb-8">
      <h1 class="text-2xl font-bold">Khám phá anime</h1>
    </div>

    <div
      class="mb-8 flex flex-col gap-4 rounded-xl bg-zinc-900/50 p-4 ring-1 ring-white/10 md:flex-row md:flex-wrap md:items-end"
    >
      <label class="flex min-w-[200px] flex-1 flex-col gap-1 text-sm">
        <span class="text-zinc-500">Từ khóa</span>
        <input
          v-model="q"
          type="search"
          placeholder="Tên phim..."
          class="rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
        />
      </label>
      <label class="flex min-w-[160px] flex-col gap-1 text-sm">
        <span class="text-zinc-500">Thể loại</span>
        <select
          v-model="genreId"
          class="rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
        >
          <option value="">Tất cả</option>
          <option v-for="g in genres" :key="g.id" :value="g.id">{{ g.name }}</option>
        </select>
      </label>
      <label class="flex min-w-[140px] flex-col gap-1 text-sm">
        <span class="text-zinc-500">Loại</span>
        <select
          v-model="type"
          class="rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
        >
          <option value="">Tất cả</option>
          <option value="TV_SERIES">TV Series</option>
          <option value="MOVIE">Movie</option>
          <option value="OVA">OVA</option>
          <option value="SPECIAL">Special</option>
        </select>
      </label>
      <label class="flex min-w-[160px] flex-col gap-1 text-sm">
        <span class="text-zinc-500">Trạng thái</span>
        <select
          v-model="status"
          class="rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
        >
          <option value="">Tất cả</option>
          <option value="ONGOING">Đang chiếu</option>
          <option value="COMPLETED">Hoàn thành</option>
          <option value="UPCOMING">Sắp chiếu</option>
        </select>
      </label>
    </div>

    <div v-if="err" class="mb-4 text-red-400">{{ err }}</div>
    <div v-if="loading" class="py-20 text-center text-zinc-500">Đang tải…</div>
    <div v-else class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-6">
      <MovieCard v-for="m in movies" :key="m.id" :movie="m" />
    </div>

    <div v-if="!loading && totalPages > 1" class="mt-10 flex justify-center gap-2">
      <button
        type="button"
        class="rounded-lg border border-white/15 px-4 py-2 text-sm disabled:opacity-40"
        :disabled="page <= 1"
        @click="goPage(page - 1)"
      >
        Trước
      </button>
      <span class="px-4 py-2 text-sm text-zinc-400">Trang {{ page }} / {{ totalPages }}</span>
      <button
        type="button"
        class="rounded-lg border border-white/15 px-4 py-2 text-sm disabled:opacity-40"
        :disabled="page >= totalPages"
        @click="goPage(page + 1)"
      >
        Sau
      </button>
    </div>
  </AppShell>
</template>
