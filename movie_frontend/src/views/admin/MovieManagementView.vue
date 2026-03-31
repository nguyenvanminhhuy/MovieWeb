<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AppShell from '../../components/AppShell.vue'
import * as adminApi from '../../api/adminApi'
import type { MovieResponse } from '../../api/types'
import { mediaUrl } from '../../utils/media'
import {
  seedTopAnime,
  checkBackendStatus,
  seedFromKitsu,
  seedFromTMDb,
  seedFromTVMaze,
  seedFromAniList,
} from '../../utils/animeSeeder'

const movies = ref<MovieResponse[]>([])
const loading = ref(true)
const seeding = ref(false)
const serverOnline = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)

async function fetchMovies() {
  loading.value = true
  err.value = ''

  // 1. Kiểm tra Backend trước
  const isOnline = await checkBackendStatus()
  serverOnline.value = isOnline
  if (!isOnline) {
    err.value = 'LỖI KẾT NỐI: Backend (Cổng 8080) chưa được bật. Hãy khởi động server Java của bạn.'
    loading.value = false
    return
  }

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

async function handleSeed() {
  const source = prompt(
    'Chọn nguồn nạp Anime/Phim:\n1: Jikan (MyAnimeList)\n2: Kitsu (Anime)\n3: TMDb (Phim lẻ)\n4: TVMaze (Phim US/UK)\n5: AniList (Anime Xịn)',
    '1',
  )
  if (!source) return

  const limitStr = prompt('Nhập số lượng muốn lấy (ví dụ: 10):', '10')
  const limit = parseInt(limitStr || '0')
  if (isNaN(limit) || limit <= 0) return

  const pageStr = prompt('Nhập trang muốn lấy (Trang 1: Top 1-50, Trang 2: Top 51-100...):', '1')
  const pageToSeed = parseInt(pageStr || '1') || 1

  seeding.value = true
  try {
    let count = 0
    if (source === '1') {
      count = await seedTopAnime(limit) // Jikan mặc định lấy top
    } else if (source === '2') {
      count = await seedFromKitsu(limit)
    } else if (source === '3') {
      const key = prompt('Nhập TMDb API Key của bạn:')
      if (!key) throw new Error('Cần có API Key để nạp từ TMDb')
      count = await seedFromTMDb(limit, key)
    } else if (source === '4') {
      count = await seedFromTVMaze(limit, pageToSeed - 1) // TVMaze page bắt đầu từ 0
    } else if (source === '5') {
      count = await seedFromAniList(limit, pageToSeed)
    }

    alert(`XONG! Đã nạp thành công ${count} bộ dữ liệu từ trang ${pageToSeed} vào hệ thống.`)
    await fetchMovies()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi nạp dữ liệu')
  } finally {
    seeding.value = false
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

function changePage(p: number) {
  page.value = p
  fetchMovies()
}

onMounted(fetchMovies)
</script>

<template>
  <AppShell>
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold flex items-center gap-3">
            Quản lý phim
            <span
              v-if="!serverOnline"
              class="text-xs font-normal bg-red-500/10 text-red-400 px-2 py-0.5 rounded border border-red-500/20"
            >
              Backend Offline 🔴
            </span>
            <span
              v-else
              class="text-xs font-normal bg-emerald-500/10 text-emerald-400 px-2 py-0.5 rounded border border-emerald-500/20"
            >
              Backend Online 🟢
            </span>
          </h1>
          <p class="mt-1 text-zinc-400">Điều chỉnh và nạp dữ liệu phim cho hệ thống</p>
        </div>

        <div class="flex gap-3">
          <button
            @click="handleSeed()"
            :disabled="seeding || !serverOnline"
            :class="[
              'rounded-xl border border-zinc-700 bg-zinc-800 px-5 py-2.5 font-semibold text-zinc-300 transition-all focus:ring-2 focus:ring-violet-500/50 outline-none',
              !serverOnline || seeding
                ? 'opacity-50 cursor-not-allowed'
                : 'hover:bg-zinc-700 active:scale-95',
            ]"
          >
            <span v-if="seeding" class="flex items-center gap-2">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                <circle
                  class="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                  fill="none"
                ></circle>
                <path
                  class="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
              Đang nạp...
            </span>
            <span v-else>Tự động nạp dữ liệu</span>
          </button>

          <button
            class="rounded-xl bg-violet-600 px-6 py-2.5 font-semibold text-white hover:bg-violet-500 shadow-lg shadow-violet-500/20 active:scale-95 transition-all"
          >
            Thêm mới
          </button>
        </div>
      </div>

      <div
        v-if="err"
        class="rounded-xl border border-red-500/30 bg-red-950/20 p-4 flex items-center gap-3"
      >
        <div class="h-2 w-2 rounded-full bg-red-500 animate-pulse"></div>
        <p class="text-sm font-medium text-red-400">{{ err }}</p>
        <button
          v-if="!serverOnline"
          @click="fetchMovies()"
          class="ml-auto text-xs underline text-red-400 hover:text-white"
        >
          Thử lại
        </button>
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
          @click="changePage(p)"
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
