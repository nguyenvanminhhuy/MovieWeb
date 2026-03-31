<script setup lang="ts">
import { RouterLink } from 'vue-router'
import AppShell from '../../components/AppShell.vue'
</script>

<template>
  <AppShell>
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <h1 class="text-3xl font-bold">Quản lý hệ thống</h1>
      </div>

      <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div class="group relative rounded-2xl border border-white/10 bg-zinc-900/50 p-6 backdrop-blur-sm transition-all hover:bg-white/5">
          <h2 class="text-lg font-semibold text-zinc-400">Phim</h2>
          <p class="mt-2 text-3xl font-bold">Quản lý</p>
          <div class="mt-4">
            <RouterLink 
              :to="{ name: 'admin-movies' }"
              class="inline-flex items-center text-sm font-medium text-violet-400 hover:text-violet-300"
            >
              Xem danh sách phim <span class="ml-1 transition-transform group-hover:translate-x-1">&rarr;</span>
            </RouterLink>
          </div>
        </div>

        <div class="group relative rounded-2xl border border-white/10 bg-zinc-900/50 p-6 backdrop-blur-sm transition-all hover:bg-white/5">
          <h2 class="text-lg font-semibold text-zinc-400">Người dùng</h2>
          <p class="mt-2 text-3xl font-bold">Quản lý</p>
          <div class="mt-4">
            <RouterLink 
              :to="{ name: 'admin-users' }"
              class="inline-flex items-center text-sm font-medium text-violet-400 hover:text-violet-300"
            >
              Xem người dùng <span class="ml-1 transition-transform group-hover:translate-x-1">&rarr;</span>
            </RouterLink>
          </div>
        </div>

        <div class="group relative rounded-2xl border border-white/10 bg-zinc-900/50 p-6 backdrop-blur-sm transition-all hover:bg-white/5">
          <h2 class="text-lg font-semibold text-zinc-400">Bình luận</h2>
          <p class="mt-2 text-3xl font-bold">Duyệt</p>
          <div class="mt-4">
            <RouterLink 
              :to="{ name: 'admin-comments' }"
              class="inline-flex items-center text-sm font-medium text-violet-400 hover:text-violet-300"
            >
              Kiểm duyệt bình luận <span class="ml-1 transition-transform group-hover:translate-x-1">&rarr;</span>
            </RouterLink>
          </div>
        </div>
      </div>

      <div class="rounded-2xl border border-white/10 bg-zinc-900/50 p-8 text-center backdrop-blur-sm">
        <p class="text-zinc-500">Chưa có dữ liệu thống kê chi tiết.</p>
      </div>
    </div>
  </AppShell>
</template>
