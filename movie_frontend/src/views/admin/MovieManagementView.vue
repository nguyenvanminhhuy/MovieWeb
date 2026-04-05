<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AppShell from '../../components/AppShell.vue'
import * as adminApi from '../../api/adminApi'
import type { MovieResponse } from '../../api/types'
import { mediaUrl } from '../../utils/media'

const movies = ref<MovieResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)

async function fetchMovies() {
  loading.value = true
  try {
    const res = await adminApi.getAllMoviesAdmin(page.value, 10)
    movies.value = res.data
    totalPages.value = res.totalPages
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải danh sách phim'
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa phim này?')) return
  try {
    await adminApi.deleteMovie(id)
    await fetchMovies()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa phim')
  }
}

onMounted(fetchMovies)
</script>

<template>
  <AppShell>
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold">Quản lý phim</h1>
          <p class="mt-1 text-zinc-400">Danh sách các phim trên hệ thống</p>
        </div>
        <button
          class="rounded-xl bg-violet-600 px-6 py-2.5 font-semibold text-white hover:bg-violet-500 shadow-lg shadow-violet-500/20"
        >
          Thêm phim mới
        </button>
      </div>

      <div v-if="err" class="rounded-xl bg-red-950/50 p-4 text-red-300 ring-1 ring-red-500/30">
        {{ err }}
      </div>

      <div
        class="overflow-hidden rounded-2xl border border-white/10 bg-zinc-900/50 backdrop-blur-sm shadow-xl"
      >
        <table class="w-full text-left">
          <thead class="bg-white/5 border-b border-white/10">
            <tr>
              <th
                class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase"
              >
                Phim
              </th>
              <th
                class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase"
              >
                Loại
              </th>
              <th
                class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase"
              >
                Năm
              </th>
              <th
                class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase"
              >
                Trạng thái
              </th>
              <th
                class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase"
              >
                Hành động
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-white/5">
            <tr v-if="loading" v-for="i in 5" :key="i" class="animate-pulse">
              <td class="px-6 py-4"><div class="h-10 w-24 rounded bg-white/10"></div></td>
              <td class="px-6 py-4"><div class="h-4 w-12 rounded bg-white/10"></div></td>
              <td class="px-6 py-4"><div class="h-4 w-12 rounded bg-white/10"></div></td>
              <td class="px-6 py-4"><div class="h-4 w-12 rounded bg-white/10"></div></td>
              <td class="px-6 py-4"><div class="h-8 w-16 rounded bg-white/10"></div></td>
            </tr>
            <tr v-else v-for="m in movies" :key="m.id" class="hover:bg-white/5 transition-colors">
              <td class="px-6 py-4">
                <div class="flex items-center gap-4">
                  <img
                    :src="mediaUrl(m.poster)"
                    class="h-12 w-9 rounded object-cover shadow-md"
                    alt=""
                  />
                  <div>
                    <div class="font-semibold text-zinc-100 line-clamp-1">{{ m.title }}</div>
                    <div class="text-xs text-zinc-500">{{ m.id }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 text-sm text-zinc-400 capitalize">{{ m.type }}</td>
              <td class="px-6 py-4 text-sm text-zinc-400">{{ m.releaseYear || '-' }}</td>
              <td class="px-6 py-4">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-bold rounded-full',
                    m.status === 'COMPLETED'
                      ? 'bg-emerald-500/10 text-emerald-400 ring-1 ring-emerald-500/30'
                      : 'bg-amber-500/10 text-amber-400 ring-1 ring-amber-500/30',
                  ]"
                >
                  {{ m.status }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm">
                <div class="flex items-center gap-3">
                  <button class="text-violet-400 hover:text-violet-300 transition-colors">
                    Sửa
                  </button>
                  <button
                    @click="handleDelete(m.id)"
                    class="text-red-400 hover:text-red-300 transition-colors"
                  >
                    Xóa
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && movies.length === 0">
              <td colspan="5" class="px-6 py-12 text-center text-zinc-500">
                Không tìm thấy phim nào.
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Simple Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-2">
        <button
          v-for="p in totalPages"
          :key="p"
          @click="
            page = p
            fetchMovies()
          "
          :class="[
            'px-4 py-2 rounded-lg font-medium transition-all',
            page === p ? 'bg-violet-600 text-white' : 'bg-white/5 text-zinc-400 hover:bg-white/10',
          ]"
        >
          {{ p }}
        </button>
      </div>
    </div>
  </AppShell>
</template>
