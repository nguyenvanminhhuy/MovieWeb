<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as api from '../api/animeApi'
import type { UserResponse } from '../api/types'
import AppShell from '../components/AppShell.vue'
import { mediaUrl } from '../utils/media'

const user = ref<UserResponse | null>(null)
const loading = ref(true)
const err = ref('')

onMounted(async () => {
  loading.value = true
  err.value = ''
  try {
    user.value = await api.getMyInfo()
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không tải được'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <AppShell>
    <h1 class="mb-6 text-2xl font-bold">Tài khoản</h1>
    <div v-if="loading" class="py-20 text-center text-zinc-500">Đang tải…</div>
    <div v-else-if="err" class="text-red-400">{{ err }}</div>
    <div v-else-if="user" class="flex max-w-xl flex-col gap-6 rounded-2xl bg-zinc-900/60 p-8 ring-1 ring-white/10 sm:flex-row">
      <div class="flex shrink-0 justify-center">
        <div
          class="flex h-28 w-28 overflow-hidden rounded-full bg-zinc-800 ring-2 ring-violet-500/30"
        >
          <img
            v-if="user.avatar && mediaUrl(user.avatar)"
            :src="mediaUrl(user.avatar)"
            alt=""
            class="h-full w-full object-cover"
          />
          <div
            v-else
            class="flex h-full w-full items-center justify-center text-2xl text-zinc-500"
          >
            {{ (user.username || '?').slice(0, 1).toUpperCase() }}
          </div>
        </div>
      </div>
      <div class="min-w-0 flex-1 space-y-2 text-sm">
        <p><span class="text-zinc-500">Tên đăng nhập:</span> {{ user.username }}</p>
        <p><span class="text-zinc-500">Email:</span> {{ user.email }}</p>
        <p><span class="text-zinc-500">Họ tên:</span> {{ user.fullName || '—' }}</p>
        <p v-if="user.roles?.length">
          <span class="text-zinc-500">Vai trò:</span>
          {{ user.roles.map((r) => r.name).join(', ') }}
        </p>
        <p v-if="user.createdAt" class="text-xs text-zinc-600">
          Tham gia: {{ new Date(user.createdAt).toLocaleDateString('vi-VN') }}
        </p>
      </div>
    </div>
  </AppShell>
</template>
