<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AppShell from '../components/AppShell.vue'
import * as api from '../api/animeApi'

const notifications = ref<any[]>([])
const loading = ref(true)

async function fetchNotifications() {
  loading.value = true
  try {
    const res = await api.getMyNotifications(1, 50)
    notifications.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function markRead(id: string) {
  try {
    await api.markNotificationRead(id)
    const n = notifications.value.find(x => x.id === id)
    if (n) n.read = true
  } catch (e) {
    console.error(e)
  }
}

async function markAllRead() {
  try {
    await api.markAllNotificationsRead()
    notifications.value.forEach(n => n.read = true)
  } catch (e) {
    console.error(e)
  }
}

onMounted(fetchNotifications)
</script>

<template>
  <AppShell>
    <div class="mx-auto max-w-3xl">
      <div class="flex items-center justify-between mb-8">
        <h1 class="text-3xl font-bold text-white">Thông báo</h1>
        <button 
          v-if="notifications.some(n => !n.read)"
          @click="markAllRead" 
          class="text-sm font-medium text-violet-400 hover:text-violet-300 transition"
        >
          Đánh dấu tất cả đã đọc
        </button>
      </div>

      <div v-if="loading" class="space-y-4">
        <div v-for="i in 5" :key="i" class="h-24 w-full rounded-2xl bg-zinc-900/50 animate-pulse"></div>
      </div>

      <div v-else-if="notifications.length === 0" class="py-20 text-center">
        <div class="inline-flex h-16 w-16 items-center justify-center rounded-full bg-zinc-900 text-zinc-500 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
        </div>
        <p class="text-zinc-500">Bạn chưa có thông báo nào.</p>
      </div>

      <div v-else class="space-y-3">
        <div 
          v-for="n in notifications" 
          :key="n.id" 
          @click="!n.read && markRead(n.id)"
          :class="[
            'group relative rounded-2xl p-5 transition-all cursor-pointer ring-1',
            n.read 
              ? 'bg-zinc-900/30 ring-white/5 grayscale-[0.5] opacity-80' 
              : 'bg-zinc-900/80 ring-white/10 hover:ring-white/20'
          ]"
        >
          <div class="flex gap-4">
            <div :class="['mt-1 flex h-2 w-2 shrink-0 rounded-full', n.read ? 'bg-transparent' : 'bg-violet-500 shadow-[0_0_8px_rgba(139,92,246,0.5)]']"></div>
            <div class="flex-1 min-w-0">
              <div class="flex justify-between items-start gap-4">
                <h3 :class="['font-bold leading-none mb-1', n.read ? 'text-zinc-400' : 'text-white']">
                  {{ n.title }}
                </h3>
                <span class="text-[10px] font-medium uppercase tracking-wider text-zinc-500 shrink-0">
                  {{ new Date(n.createdAt).toLocaleDateString('vi-VN') }}
                </span>
              </div>
              <p :class="['text-sm line-clamp-2', n.read ? 'text-zinc-500' : 'text-zinc-300']">
                {{ n.message }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppShell>
</template>
