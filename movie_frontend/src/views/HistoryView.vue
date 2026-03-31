<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import * as api from '../api/animeApi'
import type { WatchHistoryResponse } from '../api/types'
import AppShell from '../components/AppShell.vue'
import { mediaUrl } from '../utils/media'

const items = ref<WatchHistoryResponse[]>([])
const loading = ref(true)
const err = ref('')

onMounted(async () => {
  loading.value = true
  err.value = ''
  try {
    const res = await api.getHistory(1, 30)
    items.value = res.data
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không tải được'
  } finally {
    loading.value = false
  }
})

function formatTime(sec: number | undefined) {
  if (sec == null) return ''
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}
</script>

<template>
  <AppShell>
    <h1 class="mb-4 text-2xl font-bold">Đã xem gần đây</h1>
    <div v-if="loading" class="py-20 text-center text-zinc-500">Đang tải…</div>
    <div v-else-if="err" class="text-red-400">{{ err }}</div>
    <ul v-else class="space-y-3">
      <li
        v-for="h in items"
        :key="h.id"
        class="flex gap-4 rounded-xl bg-zinc-900/50 p-4 ring-1 ring-white/10"
      >
        <div class="h-24 w-16 shrink-0 overflow-hidden rounded-lg bg-zinc-800">
          <img
            v-if="h.movie?.poster && mediaUrl(h.movie.poster)"
            :src="mediaUrl(h.movie.poster)"
            :alt="h.movie.title"
            class="h-full w-full object-cover"
          />
        </div>
        <div class="min-w-0 flex-1">
          <RouterLink
            v-if="h.movie && h.episode"
            :to="{ name: 'watch', params: { movieId: h.movie.id }, query: { episode: h.episode.id } }"
            class="font-semibold text-white hover:text-violet-400"
          >
            {{ h.movie.title }}
          </RouterLink>
          <p class="text-sm text-zinc-500">
            Đã xem
            <span v-if="h.episode"> Tập {{ h.episode.episodeNumber }}</span>
            <span v-if="h.progress != null"> — {{ formatTime(h.progress) }}</span>
          </p>
          <p v-if="h.updatedAt" class="mt-1 text-xs text-zinc-600">
            {{ new Date(h.updatedAt).toLocaleString('vi-VN') }}
          </p>
        </div>
      </li>
    </ul>
    <p v-if="!loading && !err && !items.length" class="mt-8 text-center text-zinc-500">
      Chưa có lịch sử xem.
    </p>
  </AppShell>
</template>
