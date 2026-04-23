<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import * as api from '../api/animeApi'

const route = useRoute()
const { isLoggedIn, isAdmin, user, logout, refreshProfile } = useAuth()
const unreadCount = ref(0)

onMounted(async () => {
  if (localStorage.getItem('access_token')) {
    void refreshProfile()
    try {
      unreadCount.value = await api.getUnreadNotificationCount()
    } catch (e) {
      console.error(e)
    }
  }
})

function isActive(name: string) {
  return route.name === name
}
</script>

<template>
  <div class="min-h-screen bg-zinc-950 text-zinc-100">
    <header class="sticky top-0 z-50 border-b border-white/10 bg-zinc-950/90 backdrop-blur-md">
      <div class="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3">
        <RouterLink to="/" class="flex items-center gap-2 text-xl font-bold tracking-tight">
          <span class="bg-linear-to-r from-violet-400 to-fuchsia-400 bg-clip-text text-transparent"
            >AnimeStream</span
          >
        </RouterLink>

        <nav
          class="flex max-w-[min(100%,320px)] flex-wrap items-center justify-end gap-1 sm:max-w-none"
        >
          <RouterLink
            to="/"
            class="rounded-lg px-3 py-2 text-sm font-medium transition"
            :class="isActive('home') ? 'bg-white/10 text-white' : 'text-zinc-400 hover:text-white'"
          >
            Trang chủ
          </RouterLink>
          <RouterLink
            to="/browse"
            class="rounded-lg px-3 py-2 text-sm font-medium transition"
            :class="
              isActive('browse') ? 'bg-white/10 text-white' : 'text-zinc-400 hover:text-white'
            "
          >
            Khám phá
          </RouterLink>
          <RouterLink
            v-if="isLoggedIn"
            to="/favorites"
            class="rounded-lg px-3 py-2 text-sm font-medium transition"
            :class="
              isActive('favorites') ? 'bg-white/10 text-white' : 'text-zinc-400 hover:text-white'
            "
          >
            Yêu thích
          </RouterLink>
          <RouterLink
            v-if="isLoggedIn"
            to="/history"
            class="rounded-lg px-3 py-2 text-sm font-medium transition"
            :class="
              isActive('history') ? 'bg-white/10 text-white' : 'text-zinc-400 hover:text-white'
            "
          >
            Đã xem
          </RouterLink>
          <RouterLink
            v-if="isLoggedIn && isAdmin"
            to="/admin"
            class="rounded-lg px-3 py-2 text-sm font-medium transition"
            :class="
              route.path.startsWith('/admin')
                ? 'bg-white/10 text-white'
                : 'text-zinc-400 hover:text-white'
            "
          >
            Quản lý
          </RouterLink>
        </nav>

        <div class="flex items-center gap-3">
          <template v-if="isLoggedIn">
            <RouterLink
              to="/notifications"
              class="relative rounded-lg p-2 text-zinc-400 hover:bg-white/5 hover:text-white transition"
            >
              <span class="sr-only">Thông báo</span>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                />
              </svg>
              <span
                v-if="unreadCount > 0"
                class="absolute top-1.5 right-1.5 flex h-4 w-4 items-center justify-center rounded-full bg-violet-600 text-[10px] font-bold text-white ring-2 ring-zinc-950"
              >
                {{ unreadCount > 9 ? '9+' : unreadCount }}
              </span>
            </RouterLink>
            <RouterLink
              to="/profile"
              class="hidden max-w-40 truncate text-sm font-medium text-zinc-300 hover:text-white sm:inline"
            >
              {{ user?.fullName || user?.username }}
            </RouterLink>
            <button
              type="button"
              class="rounded-lg border border-white/15 px-3 py-1.5 text-sm font-medium hover:bg-white/10 transition"
              @click="logout()"
            >
              Đăng xuất
            </button>
          </template>
          <template v-else>
            <RouterLink
              to="/login"
              class="rounded-lg px-3 py-1.5 text-sm text-zinc-300 hover:text-white"
            >
              Đăng nhập
            </RouterLink>
            <RouterLink
              to="/register"
              class="rounded-lg bg-violet-600 px-3 py-1.5 text-sm font-medium hover:bg-violet-500"
            >
              Đăng ký
            </RouterLink>
          </template>
        </div>
      </div>
    </header>

    <main class="mx-auto max-w-7xl px-4 py-8">
      <slot />
    </main>

    <footer class="border-t border-white/10 py-8 text-center text-sm text-zinc-500">
      AnimeStream — xem anime theo API backend của bạn.
    </footer>
  </div>
</template>
