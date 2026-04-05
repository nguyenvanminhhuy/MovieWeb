<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as api from '../api/animeApi'
import type { MovieResponse } from '../api/types'
import AppShell from '../components/AppShell.vue'
import MovieCard from '../components/MovieCard.vue'

const movies = ref<MovieResponse[]>([])
const loading = ref(true)
const err = ref('')

onMounted(async () => {
  loading.value = true
  err.value = ''
  try {
    const res = await api.getFavorites(1, 48)
    movies.value = res.data
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không tải được'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <AppShell>
    <h1 class="mb-4 text-2xl font-bold">Phim yêu thích</h1>
    <div v-if="loading" class="py-20 text-center text-zinc-500">Đang tải…</div>
    <div v-else-if="err" class="text-red-400">{{ err }}</div>
    <div v-else-if="!movies.length" class="mt-8 rounded-xl bg-zinc-900/50 p-8 text-center text-zinc-500 ring-1 ring-white/10">
      Chưa có phim nào trong danh sách.
    </div>
    <div v-else class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-6">
      <MovieCard v-for="m in movies" :key="m.id" :movie="m" />
    </div>
  </AppShell>
</template>
