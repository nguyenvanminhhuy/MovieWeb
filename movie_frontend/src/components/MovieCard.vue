<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import type { MovieResponse } from '../api/types'
import { mediaUrl } from '../utils/media'

const props = defineProps<{
  movie: MovieResponse
  compact?: boolean
}>()

const poster = computed(() => {
  const u = mediaUrl(props.movie.poster)
  return u || ''
})
</script>

<template>
  <RouterLink
    :to="{ name: 'movie', params: { id: movie.id } }"
    class="group block overflow-hidden rounded-xl bg-zinc-900/80 ring-1 ring-white/10 transition hover:ring-violet-500/50 hover:shadow-lg hover:shadow-violet-500/10"
  >
    <div
      class="relative aspect-[2/3] overflow-hidden bg-gradient-to-br from-violet-900/40 to-zinc-900"
      :class="compact ? 'max-h-52' : ''"
    >
      <img
        v-if="poster"
        :src="poster"
        :alt="movie.title"
        class="h-full w-full object-cover transition duration-300 group-hover:scale-105"
        loading="lazy"
      />
      <div
        v-else
        class="flex h-full w-full items-center justify-center p-4 text-center text-sm font-medium text-zinc-400"
      >
        {{ movie.title }}
      </div>
      <div
        class="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/90 to-transparent px-3 pb-3 pt-12"
      >
        <p class="line-clamp-2 text-sm font-semibold text-white">{{ movie.title }}</p>
        <p class="mt-1 text-xs text-zinc-400">
          <span v-if="movie.releaseYear">{{ movie.releaseYear }}</span>
          <span v-if="movie.rating != null" class="ml-2">★ {{ movie.rating.toFixed(1) }}</span>
        </p>
      </div>
    </div>
  </RouterLink>
</template>
