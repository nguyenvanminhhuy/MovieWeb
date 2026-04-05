<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import type { EpisodeResponse } from '../../api/types'

const route = useRoute()
const movieId = route.params.movieId as string
const episodes = ref<EpisodeResponse[]>([])
const loading = ref(true)
const saving = ref(false)
const showAddModal = ref(false)
const editingEp = ref<Partial<EpisodeResponse> | null>(null)
const movieTitle = ref('Phim')

const form = ref<Partial<EpisodeResponse>>({
  episodeNumber: 1,
  title: '',
  description: '',
  duration: 24,
  thumbnailUrl: '',
})

async function fetchEpisodes() {
  loading.value = true
  try {
    const res = await adminApi.getEpisodesByMovieAdmin(movieId)
    episodes.value = res.sort((a, b) => a.episodeNumber - b.episodeNumber)
    if (res.length > 0) {
      // Could fetch movie title here if needed
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  saving.value = true
  try {
    const payload = { ...form.value, movieId }
    if (editingEp.value?.id) {
      await adminApi.updateEpisode(editingEp.value.id, payload)
    } else {
      await adminApi.createEpisode(payload)
    }
    showAddModal.value = false
    await fetchEpisodes()
  } catch (e) {
    alert('Lỗi: ' + e)
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Xóa tập phim này?')) return
  try {
    await adminApi.deleteEpisode(id)
    await fetchEpisodes()
  } catch (e) {
    alert('Lỗi: ' + e)
  }
}

function openAdd() {
  editingEp.value = null
  form.value = {
    episodeNumber: (episodes.value[episodes.value.length - 1]?.episodeNumber || 0) + 1,
    title: '',
    description: '',
    duration: 24,
    thumbnailUrl: '',
  }
  showAddModal.value = true
}

function openEdit(ep: EpisodeResponse) {
  editingEp.value = ep
  form.value = {
    episodeNumber: ep.episodeNumber,
    title: ep.title,
    description: ep.description,
    duration: ep.duration,
    thumbnailUrl: ep.thumbnailUrl,
  }
  showAddModal.value = true
}

// Video Source & Subtitle Management
const showMediaModal = ref(false)
const activeEpId = ref<string | null>(null)
const activeSources = ref<any[]>([])
const activeSubtitles = ref<any[]>([])

async function openMedia(epId: string) {
  activeEpId.value = epId
  showMediaModal.value = true
  await fetchMedia()
}

async function fetchMedia() {
  if (!activeEpId.value) return
  try {
    const [s, sub] = await Promise.all([
      adminApi.getVideoSourcesByEpisodeAdmin(activeEpId.value),
      adminApi.getSubtitlesByEpisodeAdmin(activeEpId.value),
    ])
    activeSources.value = s
    activeSubtitles.value = sub
  } catch (e) {
    console.error(e)
  }
}

async function addSource() {
  const url = prompt('Nhập URL video:')
  if (!url) return
  const quality = prompt('Chất lượng (720p, 1080p...):', '720p') || '720p'
  try {
    await adminApi.createVideoSource({ episodeId: activeEpId.value!, videoUrl: url, quality })
    await fetchMedia()
  } catch (e) {
    alert(e)
  }
}

async function deleteSource(id: string) {
  if (!confirm('Xóa nguồn này?')) return
  try {
    await adminApi.deleteVideoSource(id)
    await fetchMedia()
  } catch (e) {
    alert(e)
  }
}

async function addSubtitle() {
  const url = prompt('Nhập URL phụ đề (.vtt):')
  if (!url) return
  const lang = prompt('Ngôn ngữ:', 'Vietnamese') || 'Vietnamese'
  try {
    await adminApi.createSubtitle({ episodeId: activeEpId.value!, subtitleUrl: url, language: lang })
    await fetchMedia()
  } catch (e) {
    alert(e)
  }
}

async function deleteSubtitle(id: string) {
  if (!confirm('Xóa phụ đề này?')) return
  try {
    await adminApi.deleteSubtitle(id)
    await fetchMedia()
  } catch (e) {
    alert(e)
  }
}

onMounted(fetchEpisodes)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <RouterLink to="/admin/movies" class="back-link">← Quay lại danh sách phim</RouterLink>
          <h1 class="page-title">Quản lý tập phim</h1>
          <p class="page-subtitle">ID Phim: {{ movieId }}</p>
        </div>
        <button @click="openAdd" class="btn btn-primary">
          Thêm tập mới
        </button>
      </div>

      <div class="episodes-container mt-6">
        <div v-if="loading" v-for="i in 5" :key="i" class="ep-skeleton"></div>
        <div v-else v-for="ep in episodes" :key="ep.id" class="ep-card">
          <div class="ep-number">Tập {{ ep.episodeNumber }}</div>
          <div class="ep-info">
            <div class="ep-title">{{ ep.title || 'Không có tiêu đề' }}</div>
            <div class="ep-meta">
              <span>⏱️ {{ ep.duration || 0 }} phút</span>
            </div>
          </div>
          <div class="ep-actions">
            <button @click="openMedia(ep.id)" class="action-btn media">Nguồn & Phụ đề</button>
            <button @click="openEdit(ep)" class="action-btn edit">Sửa</button>
            <button @click="handleDelete(ep.id)" class="action-btn delete">Xóa</button>
          </div>
        </div>
        <div v-if="!loading && episodes.length === 0" class="empty-state">
          Chưa có tập phim nào.
        </div>
      </div>

      <!-- Add/Edit Modal -->
      <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
        <form @submit.prevent="handleSave" class="modal">
          <h3>{{ editingEp ? 'Sửa tập phim' : 'Thêm tập phim' }}</h3>
          <div class="form-grid mt-4">
            <div class="form-group">
              <label>Số tập</label>
              <input v-model.number="form.episodeNumber" type="number" class="form-input" required />
            </div>
            <div class="form-group">
              <label>Tiêu đề</label>
              <input v-model="form.title" class="form-input" placeholder="Tên tập phim..." />
            </div>
            <div class="form-group">
              <label>Thời lượng (phút)</label>
              <input v-model.number="form.duration" type="number" class="form-input" />
            </div>
            <div class="form-group">
              <label>Thumbnail URL</label>
              <input v-model="form.thumbnailUrl" class="form-input" placeholder="https://..." />
            </div>
            <div class="form-group span-2">
              <label>Mô tả</label>
              <textarea v-model="form.description" class="form-input" rows="3"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="showAddModal = false" class="btn btn-ghost">Hủy</button>
            <button type="submit" :disabled="saving" class="btn btn-primary">
              {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Media Modal (Sources & Subtitles) -->
      <div v-if="showMediaModal" class="modal-overlay" @click.self="showMediaModal = false">
        <div class="modal wide">
          <div class="modal-header">
            <h3>Quản lý Nguồn & Phụ đề</h3>
            <button @click="showMediaModal = false" class="modal-close">✕</button>
          </div>
          <div class="modal-body grid-2">
            <div class="media-section">
              <div class="flex justify-between items-center mb-3">
                <h4 class="font-bold">Nguồn Video</h4>
                <button @click="addSource" class="btn-xs">Thêm</button>
              </div>
              <ul class="media-list">
                <li v-for="s in activeSources" :key="s.id">
                  <span class="text-xs truncate flex-1">{{ s.videoUrl }}</span>
                  <span class="badge">{{ s.quality }}</span>
                  <button @click="deleteSource(s.id)" class="text-red-400 hover:text-red-300">✕</button>
                </li>
                <li v-if="!activeSources.length" class="text-xs text-zinc-500">Chưa có nguồn.</li>
              </ul>
            </div>
            <div class="media-section">
              <div class="flex justify-between items-center mb-3">
                <h4 class="font-bold">Phụ đề</h4>
                <button @click="addSubtitle" class="btn-xs">Thêm</button>
              </div>
              <ul class="media-list">
                <li v-for="s in activeSubtitles" :key="s.id">
                  <span class="text-xs truncate flex-1">{{ s.subtitleUrl }}</span>
                  <span class="badge">{{ s.language }}</span>
                  <button @click="deleteSubtitle(s.id)" class="text-red-400 hover:text-red-300">✕</button>
                </li>
                <li v-if="!activeSubtitles.length" class="text-xs text-zinc-500">Chưa có phụ đề.</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 20px; }
.back-link { font-size: 13px; color: #7c3aed; text-decoration: none; margin-bottom: 8px; display: inline-block; font-weight: 500; }
.page-title { font-size: 24px; font-weight: 800; color: #f4f4f5; margin: 0; }
.page-subtitle { font-size: 13px; color: #71717a; margin-top: 4px; font-family: monospace; }

.episodes-container { display: flex; flex-direction: column; gap: 10px; }
.ep-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 12px; padding: 16px; display: flex; align-items: center; gap: 20px; }
.ep-number { width: 60px; height: 60px; background: rgba(124,58,237,0.1); border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 800; color: #a78bfa; flex-shrink: 0; }
.ep-info { flex: 1; }
.ep-title { font-size: 15px; font-weight: 600; color: #f4f4f5; margin-bottom: 4px; }
.ep-meta { display: flex; gap: 12px; font-size: 12px; color: #71717a; }
.ep-actions { display: flex; gap: 10px; }

.action-btn { padding: 6px 14px; border-radius: 8px; font-size: 12px; font-weight: 600; border: none; cursor: pointer; transition: all 0.15s; }
.action-btn.edit { background: rgba(124,58,237,0.1); color: #a78bfa; }
.action-btn.media { background: rgba(16,185,129,0.1); color: #34d399; }
.action-btn.delete { background: rgba(239,68,68,0.1); color: #f87171; }

.ep-skeleton { background: #18181b; border-radius: 12px; height: 92px; animation: pulse 1.5s infinite; }
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }

.btn { padding: 9px 20px; border-radius: 10px; font-size: 14px; font-weight: 700; cursor: pointer; border: none; transition: all 0.2s; }
.btn:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-primary { background: linear-gradient(to right, #7c3aed, #a855f7); color: white; }
.btn-ghost { background: transparent; color: #71717a; }
.btn-xs { padding: 4px 10px; border-radius: 6px; background: #7c3aed; color: white; font-size: 11px; font-weight: 600; border: none; cursor: pointer; }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.8); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 100; padding: 20px; }
.modal { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 20px; padding: 24px; width: 100%; max-width: 500px; }
.modal.wide { max-width: 800px; }
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.modal-close { background: none; border: none; color: #71717a; cursor: pointer; font-size: 20px; }
.modal h3 { color: white; margin: 0; font-size: 18px; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; }

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group.span-2 { grid-column: span 2; }
.form-group label { font-size: 12px; color: #71717a; font-weight: 600; }
.form-input { background: #09090b; border: 1px solid #27272a; border-radius: 8px; padding: 10px; color: white; font-size: 14px; outline: none; }
.form-input:focus { border-color: #7c3aed; }

.grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.media-list { display: flex; flex-direction: column; gap: 8px; margin-top: 10px; }
.media-list li { background: #09090b; border-radius: 8px; padding: 8px 12px; display: flex; align-items: center; gap: 10px; border: 1px solid #27272a; }
.badge { font-size: 10px; font-weight: 800; padding: 2px 6px; border-radius: 4px; background: #27272a; color: #a1a1aa; }
.truncate { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
