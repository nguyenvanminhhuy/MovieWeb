<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import * as animeApi from '../../api/animeApi'
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
const totalElements = ref(0)
const search = ref('')
const deletingIds = ref(new Set<string>())

const showAddModal = ref(false)
const editingMovie = ref<Partial<MovieResponse> | null>(null)
const form = ref<any>({
  title: '',
  description: '',
  posterUrl: '',
  bannerUrl: '',
  type: 'SERIES',
  status: 'ONGOING',
  releaseDate: '',
  trailerUrl: '',
  genres: [] as string[],
  studios: [] as string[],
})
const saving = ref(false)

const allGenres = ref<{ id: string; name: string }[]>([])
const allStudios = ref<{ id: string; name: string }[]>([])

async function fetchMetadata() {
  try {
    const [g, s] = await Promise.all([
      adminApi.getAllGenresAdmin(1, 200),
      adminApi.getAllStudiosAdmin(1, 200),
    ])
    allGenres.value = g.data
    allStudios.value = s.data
  } catch (e) {
    console.error('Lỗi tải metadata:', e)
  }
}

async function fetchMovies() {
  loading.value = true
  err.value = ''
  const isOnline = await checkBackendStatus()
  serverOnline.value = isOnline
  if (!isOnline) {
    err.value = 'LỖI KẾT NỐI: Backend (Cổng 8080) chưa được bật.'
    loading.value = false
    return
  }
  try {
    let res
    if (search.value.trim()) {
      res = await animeApi.searchMovies({ query: search.value.trim(), page: page.value, size: 12 })
    } else {
      res = await adminApi.getAllMoviesAdmin(page.value, 12)
    }
    movies.value = res.data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
    await fetchMetadata()
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải danh sách phim'
  } finally {
    loading.value = false
  }
}

async function handleSeed() {
  const source = prompt(
    'Chọn nguồn:\n1: Jikan (MAL)\n2: Kitsu\n3: TMDb\n4: TVMaze\n5: AniList',
    '1',
  )
  if (!source) return
  const limit = parseInt(prompt('Số lượng (VD: 20):', '20') || '0')
  if (!limit || limit <= 0) return
  const pg = parseInt(prompt('Trang (1=Top 1-50, 2=Top 51-100):', '1') || '1') || 1

  seeding.value = true
  try {
    let count = 0
    if (source === '1') count = await seedTopAnime(limit)
    else if (source === '2') count = await seedFromKitsu(limit)
    else if (source === '3') {
      const key = prompt('TMDb API Key:')
      if (!key) throw new Error('Cần API Key')
      count = await seedFromTMDb(limit, key)
    } else if (source === '4') count = await seedFromTVMaze(limit, pg - 1)
    else if (source === '5') count = await seedFromAniList(limit, pg)
    alert(`✅ Đã nạp ${count} bộ phim vào hệ thống!`)
    await fetchMovies()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi nạp dữ liệu')
  } finally {
    seeding.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa bộ phim này?')) return

  deletingIds.value = new Set([...deletingIds.value, id])
  try {
    await adminApi.deleteMovie(id)
    await fetchMovies()
  } catch (e) {
    const msg = e instanceof Error ? e.message : 'Lỗi khi xóa phim'
    alert(
      `❌ Không thể xóa phim: ${msg}\n\nLưu ý: Có thể do phim đang có tập phim hoặc dữ liệu liên quan.`,
    )
  } finally {
    const next = new Set(deletingIds.value)
    next.delete(id)
    deletingIds.value = next
  }
}

function changePage(p: number) {
  page.value = p
  fetchMovies()
}

async function handleSave() {
  saving.value = true
  try {
    // Build payload matching the backend API format (with fallbacks for older API version)
    const payload: any = {
      title: form.value.title,
      description: form.value.description,
      posterUrl: form.value.posterUrl,
      bannerUrl: form.value.bannerUrl,
      poster: form.value.posterUrl, // fallback
      banner: form.value.bannerUrl, // fallback
      type: form.value.type,
      status: form.value.status,
      releaseDate: form.value.releaseDate || undefined,
      releaseYear: form.value.releaseDate
        ? parseInt(form.value.releaseDate.substring(0, 4))
        : undefined, // fallback
      trailerUrl: form.value.trailerUrl || undefined,
      genres: form.value.genres.map((id: string) => ({ id })), // Send as objects for Spring Boot
      genreIds: form.value.genres, // fallback
      studios: form.value.studioId ? [{ id: form.value.studioId }] : [], // Send as objects
      studioIds: form.value.studioId ? [form.value.studioId] : [], // fallback
      studio: form.value.studioId ? { id: form.value.studioId } : null, // fallback as object
      studioId: form.value.studioId || null, // fallback as string
    }

    if (editingMovie.value?.id) {
      await adminApi.updateMovie(editingMovie.value.id, payload)
      alert('✅ Cập nhật phim thành công!')
    } else {
      await adminApi.createMovie(payload)
      alert('✅ Thêm phim mới thành công!')
    }
    closeModal()
    await fetchMovies()
  } catch (e) {
    console.error('Lỗi lưu phim:', e)
    alert('❌ Lỗi: ' + (e instanceof Error ? e.message : 'Không xác định'))
  } finally {
    saving.value = false
  }
}

function openAddModal() {
  editingMovie.value = null
  form.value = {
    title: '',
    description: '',
    posterUrl: '',
    bannerUrl: '',
    type: 'SERIES',
    status: 'ONGOING',
    releaseDate: new Date().toISOString().slice(0, 10),
    trailerUrl: '',
    genres: [],
    studioId: '',
  }
  showAddModal.value = true
}

function openEditModal(m: MovieResponse) {
  editingMovie.value = m
  form.value = {
    title: m.title,
    description: m.description || '',
    posterUrl: m.posterUrl || m.poster || '',
    bannerUrl: m.bannerUrl || m.banner || '',
    type: m.type,
    status: m.status,
    releaseDate: m.releaseDate || (m.releaseYear ? `${m.releaseYear}-01-01` : ''),
    trailerUrl: m.trailerUrl || '',
    genres: m.genres ? m.genres.map((g: any) => g.id || g) : [],
    studioId:
      m.studios && m.studios.length > 0
        ? (m.studios[0] as any).id || m.studios[0]
        : m.studio
          ? typeof m.studio === 'string'
            ? m.studio
            : m.studio.id
          : '',
  }
  showAddModal.value = true
}

function closeModal() {
  showAddModal.value = false
  editingMovie.value = null
}

let searchTimeout: any = null
watch(search, () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    page.value = 1
    fetchMovies()
  }, 500)
})

onMounted(fetchMovies)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Quản lý phim</h1>
          <p class="page-subtitle">
            <span v-if="totalElements > 0"
              >{{ totalElements.toLocaleString() }} bộ phim trong hệ thống</span
            >
            <span v-else>Điều chỉnh và nạp dữ liệu phim</span>
            <span :class="['status-badge', serverOnline ? 'online' : 'offline']">
              {{ serverOnline ? '● Online' : '● Offline' }}
            </span>
          </p>
        </div>
        <div class="header-actions">
          <button
            @click="handleSeed"
            :disabled="seeding || !serverOnline"
            class="btn btn-secondary"
          >
            <svg
              v-if="!seeding"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
            <svg
              v-else
              class="spin"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"
              />
            </svg>
            {{ seeding ? 'Đang nạp...' : 'Nạp dữ liệu' }}
          </button>
          <button @click="openAddModal" class="btn btn-primary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19" stroke-linecap="round" />
              <line x1="5" y1="12" x2="19" y2="12" stroke-linecap="round" />
            </svg>
            Thêm phim
          </button>
        </div>
      </div>

      <!-- Error -->
      <div v-if="err" class="alert alert-error">
        <div class="alert-dot"></div>
        <p>{{ err }}</p>
        <button v-if="!serverOnline" @click="fetchMovies" class="alert-retry">Thử lại</button>
      </div>

      <!-- Table -->
      <div class="table-card">
        <div class="table-header">
          <div class="table-search">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8" />
              <path d="M21 21l-4.35-4.35" stroke-linecap="round" />
            </svg>
            <input v-model="search" placeholder="Tìm kiếm phim..." class="search-input" />
          </div>
          <div class="table-info">Trang {{ page }}/{{ totalPages }}</div>
        </div>

        <table class="data-table">
          <thead>
            <tr>
              <th>Phim</th>
              <th>Loại</th>
              <th>Năm</th>
              <th>Tập</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <!-- Loading -->
            <tr v-if="loading" v-for="i in 8" :key="i" class="loading-row">
              <td>
                <div class="flex gap-3 items-center">
                  <div
                    class="skeleton"
                    style="width: 40px; height: 54px; border-radius: 6px; flex-shrink: 0"
                  ></div>
                  <div>
                    <div
                      class="skeleton"
                      style="width: 130px; height: 14px; margin-bottom: 6px"
                    ></div>
                    <div class="skeleton" style="width: 80px; height: 11px"></div>
                  </div>
                </div>
              </td>
              <td><div class="skeleton" style="width: 50px; height: 12px"></div></td>
              <td><div class="skeleton" style="width: 40px; height: 12px"></div></td>
              <td><div class="skeleton" style="width: 30px; height: 12px"></div></td>
              <td>
                <div class="skeleton" style="width: 70px; height: 20px; border-radius: 20px"></div>
              </td>
              <td>
                <div class="skeleton" style="width: 80px; height: 28px; border-radius: 8px"></div>
              </td>
            </tr>
            <!-- Data -->
            <tr
              v-else
              v-for="m in movies"
              :key="m.id"
              class="data-row"
            >
              <td>
                <div class="movie-cell">
                  <img :src="mediaUrl(m.poster)" class="movie-poster" alt="" />
                  <div>
                    <div class="movie-title">{{ m.title }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span class="type-badge">{{ m.type }}</span>
              </td>
              <td class="text-muted">{{ m.releaseYear || '—' }}</td>
              <td class="text-muted">
                {{ m.episodeCount !== undefined ? m.episodeCount : '0' }} tập
              </td>
              <td>
                <span
                  :class="[
                    'status-pill',
                    m.status === 'COMPLETED'
                      ? 'completed'
                      : m.status === 'ONGOING'
                        ? 'ongoing'
                        : 'upcoming',
                  ]"
                >
                  {{
                    m.status === 'COMPLETED'
                      ? 'Hoàn thành'
                      : m.status === 'ONGOING'
                        ? 'Đang chiếu'
                        : 'Sắp ra'
                  }}
                </span>
              </td>
              <td>
                <div class="action-btns">
                  <RouterLink
                    :to="'/admin/movies/' + m.id + '/episodes'"
                    class="action-btn epis"
                    :class="{ disabled: deletingIds.has(m.id) }"
                    >Tập phim</RouterLink
                  >
                  <button
                    @click="openEditModal(m)"
                    class="action-btn edit"
                    :disabled="deletingIds.has(m.id)"
                  >
                    Sửa
                  </button>
                  <button
                    @click="handleDelete(m.id)"
                    class="action-btn delete"
                    :disabled="deletingIds.has(m.id)"
                  >
                    {{ deletingIds.has(m.id) ? 'Đang xóa...' : 'Xóa' }}
                  </button>
                </div>
              </td>
            </tr>
            <!-- Empty -->
            <tr v-if="!loading && movies.length === 0">
              <td colspan="6" class="empty-cell">
                <div class="empty-state">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <rect x="2" y="2" width="20" height="20" rx="2.18" />
                    <path d="M2 12h20M7 2v20M17 2v20" />
                  </svg>
                  <p>Không có phim nào. Hãy nạp dữ liệu!</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button @click="changePage(1)" :disabled="page === 1" class="page-btn">«</button>
        <button @click="changePage(page - 1)" :disabled="page === 1" class="page-btn">‹</button>
        <button
          v-for="p in Math.min(totalPages, 7)"
          :key="p"
          @click="changePage(p)"
          :class="['page-btn', page === p ? 'active' : '']"
        >
          {{ p }}
        </button>
        <button @click="changePage(page + 1)" :disabled="page === totalPages" class="page-btn">
          ›
        </button>
        <button @click="changePage(totalPages)" :disabled="page === totalPages" class="page-btn">
          »
        </button>
      </div>

      <!-- Add/Edit Modal -->
      <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal">
          <div class="modal-header">
            <h3>{{ editingMovie?.id ? 'Chỉnh sửa phim' : 'Thêm phim mới' }}</h3>
            <button @click="closeModal" class="modal-close">✕</button>
          </div>
          <form @submit.prevent="handleSave" class="modal-body">
            <div class="form-grid">
              <!-- Row 1: Tên phim -->
              <div class="form-group span-2">
                <label>Tên phim</label>
                <input
                  v-model="form.title"
                  class="form-input"
                  placeholder="Nhập tên phim..."
                  required
                />
              </div>

              <!-- Row 2: Mô tả -->
              <div class="form-group span-2">
                <label>Mô tả</label>
                <textarea v-model="form.description" class="form-input" rows="3"></textarea>
              </div>

              <!-- Row 3: Loại & Trạng thái -->
              <div class="form-group">
                <label>Loại</label>
                <select v-model="form.type" class="form-input">
                  <option value="SERIES">TV Series</option>
                  <option value="MOVIE">Movie</option>
                  <option value="ONA">ONA</option>
                  <option value="OVA">OVA</option>
                  <option value="SPECIAL">Special</option>
                </select>
              </div>
              <div class="form-group">
                <label>Trạng thái</label>
                <select v-model="form.status" class="form-input">
                  <option value="ONGOING">Đang chiếu</option>
                  <option value="COMPLETED">Hoàn thành</option>
                  <option value="UPCOMING">Sắp chiếu</option>
                </select>
              </div>

              <!-- Row 4: Ngày phát hành & Trailer -->
              <div class="form-group">
                <label>Ngày phát hành</label>
                <input v-model="form.releaseDate" type="date" class="form-input" />
              </div>
              <div class="form-group">
                <label>Trailer URL (Youtube)</label>
                <input v-model="form.trailerUrl" class="form-input" placeholder="https://..." />
              </div>

              <!-- Row 5: Poster & Banner -->
              <div class="form-group">
                <label>Poster URL</label>
                <input v-model="form.posterUrl" class="form-input" placeholder="https://..." />
              </div>
              <div class="form-group">
                <label>Banner URL</label>
                <input v-model="form.bannerUrl" class="form-input" placeholder="https://..." />
              </div>

              <!-- Row 6: Studio -->
              <div class="form-group span-2">
                <label>Studio liên kết (Chọn 1)</label>
                <div class="selection-grid">
                  <label v-for="s in allStudios" :key="s.id" class="selection-card">
                    <input type="radio" :value="s.id" v-model="form.studioId" name="studio_radio" />
                    <div class="card-content">
                      <div class="card-icon" v-if="form.studioId === s.id">
                        <svg
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="4"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                        >
                          <polyline points="20 6 9 17 4 12"></polyline>
                        </svg>
                      </div>
                      <span class="card-text">{{ s.name }}</span>
                    </div>
                  </label>
                </div>
              </div>

              <!-- Row 7: Thể loại -->
              <div class="form-group span-2">
                <label>Thể loại liên kết</label>
                <div class="selection-grid large">
                  <label v-for="g in allGenres" :key="g.id" class="selection-card">
                    <input type="checkbox" :value="g.id" v-model="form.genres" />
                    <div class="card-content">
                      <div class="card-icon" v-if="form.genres.includes(g.id)">
                        <svg
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="4"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                        >
                          <polyline points="20 6 9 17 4 12"></polyline>
                        </svg>
                      </div>
                      <span class="card-text">{{ g.name }}</span>
                    </div>
                  </label>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" @click="closeModal" class="btn btn-ghost">Hủy</button>
              <button type="submit" :disabled="saving" class="btn btn-primary">
                {{ saving ? 'Đang lưu...' : editingMovie?.id ? 'Cập nhật' : 'Thêm mới' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: #f4f4f5;
  margin: 0;
  letter-spacing: -0.02em;
}
.page-subtitle {
  font-size: 13px;
  color: #71717a;
  margin: 4px 0 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 20px;
}
.status-badge.online {
  background: rgba(34, 197, 94, 0.1);
  color: #4ade80;
}
.status-badge.offline {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
}

.header-actions {
  display: flex;
  gap: 10px;
}
.btn {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 9px 16px;
  border-radius: 10px;
  font-size: 13.5px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.15s;
}
.btn svg {
  width: 15px;
  height: 15px;
}
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.btn-primary {
  background: #7c3aed;
  color: white;
}
.btn-primary:not(:disabled):hover {
  background: #6d28d9;
  transform: translateY(-1px);
}
.btn-secondary {
  background: #27272a;
  color: #d4d4d8;
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.btn-secondary:not(:disabled):hover {
  background: #3f3f46;
}
.btn-ghost {
  background: transparent;
  color: #71717a;
}
.btn-ghost:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #f4f4f5;
}

.spin {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Alert */
.alert {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 13px;
}
.alert-error {
  background: rgba(239, 68, 68, 0.08);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #f87171;
}
.alert-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #ef4444;
  flex-shrink: 0;
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}
.alert-retry {
  margin-left: auto;
  font-size: 12px;
  text-decoration: underline;
  cursor: pointer;
  background: none;
  border: none;
  color: #f87171;
}

/* Table */
.table-card {
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  overflow: hidden;
}
.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  gap: 12px;
}
.table-search {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 9px;
  padding: 8px 12px;
  flex: 1;
  max-width: 300px;
}
.table-search svg {
  width: 15px;
  height: 15px;
  color: #52525b;
  flex-shrink: 0;
}
.search-input {
  background: none;
  border: none;
  outline: none;
  color: #e4e4e7;
  font-size: 13px;
  width: 100%;
}
.search-input::placeholder {
  color: #52525b;
}
.table-info {
  font-size: 12px;
  color: #52525b;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th {
  padding: 11px 16px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: #52525b;
  text-align: left;
  background: rgba(255, 255, 255, 0.02);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}
.data-table td {
  padding: 13px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}
.data-row {
  transition: background 0.12s;
}
.data-row:hover {
  background: rgba(255, 255, 255, 0.02);
}
.data-row:last-child td {
  border-bottom: none;
}

.skeleton {
  background: rgba(255, 255, 255, 0.07);
  border-radius: 4px;
  animation: pulse 1.5s infinite;
}
.flex {
  display: flex;
}
.gap-3 {
  gap: 12px;
}
.items-center {
  align-items: center;
}

.movie-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.movie-poster {
  width: 36px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  background: #27272a;
  flex-shrink: 0;
}
.movie-title {
  font-size: 13.5px;
  font-weight: 600;
  color: #e4e4e7;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.movie-id {
  font-size: 11px;
  color: #52525b;
  font-family: monospace;
  margin-top: 2px;
}

.type-badge {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  padding: 3px 8px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.06);
  color: #a1a1aa;
}
.text-muted {
  font-size: 13px;
  color: #71717a;
}
.status-pill {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
}
.status-pill.completed {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
}
.status-pill.ongoing {
  background: rgba(245, 158, 11, 0.1);
  color: #fbbf24;
}
.status-pill.upcoming {
  background: rgba(99, 102, 241, 0.1);
  color: #818cf8;
}

.action-btns {
  display: flex;
  gap: 8px;
}
.action-btn {
  padding: 5px 11px;
  border-radius: 7px;
  font-size: 12px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.13s;
}
.action-btn.edit {
  background: rgba(124, 58, 237, 0.1);
  color: #a78bfa;
}
.action-btn.edit:hover {
  background: rgba(124, 58, 237, 0.22);
}
.action-btn.epis {
  background: rgba(59, 130, 246, 0.1);
  color: #60a5fa;
}
.action-btn.epis:hover {
  background: rgba(59, 130, 246, 0.22);
}
.action-btn.delete {
  background: rgba(239, 68, 68, 0.08);
  color: #f87171;
}
.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.15);
}

.empty-cell {
  text-align: center;
  padding: 48px 20px;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #52525b;
}
.empty-state svg {
  width: 40px;
  height: 40px;
}
.empty-state p {
  font-size: 14px;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
}
.page-btn {
  padding: 7px 13px;
  border-radius: 9px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: #18181b;
  color: #71717a;
  cursor: pointer;
  transition: all 0.15s;
}
.page-btn:hover:not(:disabled) {
  background: #27272a;
  color: #e4e4e7;
}
.page-btn.active {
  background: #7c3aed;
  color: white;
  border-color: #7c3aed;
}
.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.modal {
  background: #1c1c1f;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  width: 100%;
  max-width: 640px;
  max-height: 90vh;
  overflow-y: auto;
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  position: sticky;
  top: 0;
  background: #1c1c1f;
  z-index: 10;
}
.modal-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #f4f4f5;
  margin: 0;
}
.modal-close {
  background: none;
  border: none;
  color: #71717a;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  padding: 4px;
  border-radius: 4px;
}
.modal-close:hover {
  color: #f4f4f5;
}
.modal-body {
  padding: 22px;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  position: sticky;
  bottom: 0;
  background: #1c1c1f;
  z-index: 10;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-group.span-2 {
  grid-column: span 2;
}
.form-group label {
  font-size: 12px;
  font-weight: 600;
  color: #a1a1aa;
}
.form-input {
  padding: 8px 12px;
  background: #27272a;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  color: #e4e4e7;
  font-size: 13px;
  outline: none;
  width: 100%;
}
.form-input:focus {
  border-color: #7c3aed;
}

.selection-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 12px;
  max-height: 200px;
  overflow-y: auto;
  padding: 16px;
  background: rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
  box-shadow: inset 0 4px 15px rgba(0, 0, 0, 0.2);
}
.selection-grid.large {
  max-height: 260px;
}
.selection-grid::-webkit-scrollbar {
  width: 6px;
}
.selection-grid::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}
.selection-grid::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}

.selection-card {
  cursor: pointer;
  position: relative;
  display: block;
}
.selection-card input {
  display: none;
}
.card-content {
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 14px 12px;
  text-align: center;
  transition: all 0.2s ease;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.card-text {
  font-size: 13px;
  font-weight: 500;
  color: #a1a1aa;
  z-index: 2;
  transition: color 0.2s ease;
}
.selection-card:hover .card-content {
  background: #27272a;
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}
.selection-card:hover .card-text {
  color: #e4e4e7;
}

.selection-card input:checked + .card-content {
  background: rgba(124, 58, 237, 0.08);
  border-color: #7c3aed;
  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.15);
}
.selection-card input:checked + .card-content .card-text {
  color: #c084fc;
  font-weight: 600;
}

.card-icon {
  position: absolute;
  top: -1px;
  right: -1px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 28px 28px 0;
  border-color: transparent #7c3aed transparent transparent;
  z-index: 1;
}
.card-icon svg {
  position: absolute;
  top: 3px;
  right: -25px;
  width: 12px;
  height: 12px;
  color: white;
  animation: pop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes pop {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
