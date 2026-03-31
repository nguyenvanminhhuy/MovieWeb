<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { useAuth } from '../composables/useAuth'

const route = useRoute()
const { isLoggedIn, isAdmin, user, logout, refreshProfile } = useAuth()

onMounted(() => {
  if (localStorage.getItem('access_token')) void refreshProfile()
})

function isActive(name: string) {
  return route.name === name
}
</script>

<template>
  <div class="min-h-screen bg-zinc-950 text-zinc-100">
    <header
      class="sticky top-0 z-50 border-b border-white/10 bg-zinc-950/90 backdrop-blur-md"
    >
      <div class="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3">
        <RouterLink to="/" class="flex items-center gap-2 text-xl font-bold tracking-tight">
          <span
            class="bg-gradient-to-r from-violet-400 to-fuchsia-400 bg-clip-text text-transparent"
            >AnimeStream</span
          >
        </RouterLink>

        <nav class="flex max-w-[min(100%,320px)] flex-wrap items-center justify-end gap-1 sm:max-w-none">
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
              isActive('admin') ? 'bg-white/10 text-white' : 'text-zinc-400 hover:text-white'
            "
          >
            Quản lý
          </RouterLink>
        </nav>

        <div class="flex items-center gap-2">
          <template v-if="isLoggedIn">
            <RouterLink
              to="/profile"
              class="hidden max-w-[10rem] truncate text-sm text-zinc-300 sm:inline"
            >
              {{ user?.fullName || user?.username }}
            </RouterLink>
            <button
              type="button"
              class="rounded-lg border border-white/15 px-3 py-1.5 text-sm hover:bg-white/10"
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
