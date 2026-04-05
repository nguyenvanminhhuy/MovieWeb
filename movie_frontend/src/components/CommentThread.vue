<script setup lang="ts">
import type { CommentResponse } from '../api/types'
import CommentThread from './CommentThread.vue'

defineProps<{
  comments: CommentResponse[]
  depth?: number
}>()

const emit = defineEmits<{
  reply: [parentId: string]
  like: [id: string]
}>()

function displayName(u?: { username?: string; fullName?: string }) {
  if (!u) return 'Ẩn danh'
  return u.fullName || u.username || 'Người dùng'
}
</script>

<template>
  <ul class="space-y-4" :class="depth ? 'mt-3 border-l border-white/10 pl-4' : ''">
    <li v-for="c in comments" :key="c.id" class="rounded-lg bg-zinc-900/50 p-4 ring-1 ring-white/5">
      <div class="flex items-start justify-between gap-2">
        <div>
          <p class="font-medium text-violet-300">{{ displayName(c.user) }}</p>
          <p class="mt-1 whitespace-pre-wrap text-sm text-zinc-300">{{ c.content }}</p>
          <p class="mt-2 text-xs text-zinc-500">
            {{ c.createdAt ? new Date(c.createdAt).toLocaleString('vi-VN') : '' }}
          </p>
        </div>
        <div class="flex shrink-0 gap-2">
          <button
            type="button"
            class="rounded-lg px-2 py-1 text-xs text-zinc-400 hover:bg-white/10 hover:text-white"
            @click="emit('reply', c.id)"
          >
            Trả lời
          </button>
          <button
            type="button"
            class="rounded-lg px-2 py-1 text-xs text-zinc-400 hover:bg-white/10 hover:text-white"
            @click="emit('like', c.id)"
          >
            ♥ {{ c.likes ?? 0 }}
          </button>
        </div>
      </div>
      <CommentThread
        v-if="c.replies?.length"
        :comments="c.replies"
        :depth="(depth ?? 0) + 1"
        @reply="emit('reply', $event)"
        @like="emit('like', $event)"
      />
    </li>
  </ul>
</template>
