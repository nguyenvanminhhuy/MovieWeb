<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import type { GenreResponse } from '../../api/types'

const genres = ref<GenreResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')
const showModal = ref(false)
const editing = ref<Partial<GenreResponse> | null>(null)
const formName = ref('')
const formSlug = ref('')
const formDesc = ref('')
const saving = ref(false)

async function fetchGenres() {
  loading.value = true
  err.value = ''
  try {
    const res = await adminApi.getAllGenresAdmin(page.value, 15)
    genres.value = res.data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải thể loại'
  } finally {
    loading.value = false
  }
}

function changePage(p: number) {
  page.value = p
  fetchGenres()
}

function openAdd() {
  editing.value = null
  formName.value = ''
  formSlug.value = ''
  formDesc.value = ''
  showModal.value = true
}

function openEdit(g: GenreResponse) {
  editing.value = g
  formName.value = g.name
  formSlug.value = g.slug || ''
  formDesc.value = g.description || ''
  showModal.value = true
}

function autoSlug() {
  if (!editing.value) {
    formSlug.value = formName.value.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '')
  }
}

async function handleSave() {
  if (!formName.value.trim()) return
  saving.value = true
  try {
    const payload = { name: formName.value, slug: formSlug.value, description: formDesc.value }
    if (editing.value?.id) {
      await adminApi.updateGenre(editing.value.id, payload)
    } else {
      await adminApi.createGenre(payload)
    }
    showModal.value = false
    await fetchGenres()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi lưu thể loại')
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa thể loại này?')) return
  try {
    await adminApi.deleteGenre(id)
    await fetchGenres()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa')
  }
}

onMounted(fetchGenres)

function getFiltered() {
  return genres.value.filter(g => !search.value || g.name.toLowerCase().includes(search.value.toLowerCase()))
}
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Thể loại (Genre)</h1>
          <p class="page-subtitle">{{ totalElements.toLocaleString() }} thể loại trong hệ thống</p>
        </div>
        <button @click="openAdd" class="btn btn-primary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19" stroke-linecap="round"/>
            <line x1="5" y1="12" x2="19" y2="12" stroke-linecap="round"/>
          </svg>
          Thêm thể loại
        </button>
      </div>

      <div v-if="err" class="alert alert-error">{{ err }}</div>

      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35" stroke-linecap="round"/>
        </svg>
        <input v-model="search" placeholder="Tìm thể loại..." class="search-input" />
      </div>

      <!-- Loading -->
      <div v-if="loading" class="genre-grid">
        <div v-for="i in 12" :key="i" class="skeleton-card"></div>
      </div>

      <!-- Grid -->
      <div v-else class="genre-grid">
        <div v-for="g in getFiltered()" :key="g.id" class="genre-card">
          <div class="genre-card-header">
            <div class="genre-info">
              <div class="genre-name">{{ g.name }}</div>
              <div v-if="g.slug" class="genre-slug">/{{ g.slug }}</div>
            </div>
            <div class="genre-actions-top">
               <button @click="openEdit(g)" class="icon-btn" title="Chỉnh sửa">
                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
               </button>
               <button @click="handleDelete(g.id)" class="icon-btn delete" title="Xóa">
                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
               </button>
            </div>
          </div>
          <div v-if="g.description" class="genre-desc">{{ g.description }}</div>
          <div v-else class="genre-desc empty">Chưa có mô tả cho thể loại này.</div>
        </div>
        
        <!-- Empty State -->
        <div v-if="!loading && getFiltered().length === 0" class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M4 6h16M4 12h16M4 18h7" stroke-linecap="round" />
            </svg>
          </div>
          <h3>Không tìm thấy thể loại</h3>
          <p>Thử tìm kiếm với từ khóa khác hoặc thêm thể loại mới.</p>
          <button @click="openAdd" class="btn btn-outline">Thêm mới ngay</button>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button @click="changePage(Math.max(1, page - 1))" :disabled="page === 1" class="page-btn">‹</button>
        <button v-for="p in totalPages" :key="p" @click="changePage(p)" :class="['page-btn', page === p ? 'active' : '']">{{ p }}</button>
        <button @click="changePage(Math.min(totalPages, page + 1))" :disabled="page === totalPages" class="page-btn">›</button>
      </div>

      <!-- Modal -->
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal">
          <div class="modal-header">
            <h3>{{ editing ? 'Sửa thể loại' : 'Thêm thể loại mới' }}</h3>
            <button @click="showModal = false" class="modal-close">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Tên thể loại *</label>
              <input v-model="formName" @input="autoSlug" class="form-input" placeholder="Ví dụ: Action" />
            </div>
            <div class="form-group">
              <label>Slug (URL)</label>
              <input v-model="formSlug" class="form-input" placeholder="action" />
            </div>
            <div class="form-group">
              <label>Mô tả</label>
              <textarea v-model="formDesc" class="form-input" rows="3" placeholder="Mô tả thể loại..."></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button @click="showModal = false" class="btn btn-ghost">Hủy</button>
            <button @click="handleSave" :disabled="saving || !formName" class="btn btn-primary">
              {{ saving ? 'Đang lưu...' : (editing ? 'Cập nhật' : 'Thêm mới') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 24px; }
.page-header { display: flex; align-items: center; justify-content: space-between; gap: 20px; }
.page-title { font-size: 28px; font-weight: 900; color: #fff; margin: 0; letter-spacing: -0.03em; }
.page-subtitle { font-size: 14px; color: #71717a; margin: 4px 0 0; }

.btn { display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px; border-radius: 12px; font-size: 14px; font-weight: 700; border: none; cursor: pointer; transition: all 0.2s; }
.btn svg { width: 18px; height: 18px; }
.btn:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-primary { background: #7c3aed; color: white; box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3); }
.btn-primary:not(:disabled):hover { background: #6d28d9; transform: translateY(-1px); box-shadow: 0 6px 16px rgba(124, 58, 237, 0.4); }
.btn-ghost { background: rgba(255,255,255,0.05); color: #a1a1aa; border: 1px solid rgba(255,255,255,0.1); }
.btn-ghost:hover { background: rgba(255,255,255,0.08); color: #fff; }
.btn-outline { background: transparent; border: 1px solid rgba(124, 58, 237, 0.4); color: #a78bfa; }
.btn-outline:hover { background: rgba(124, 58, 237, 0.1); }

.search-box { display: flex; align-items: center; gap: 12px; background: #18181b; border: 1px solid rgba(255,255,255,0.08); border-radius: 14px; padding: 10px 16px; width: 100%; max-width: 400px; transition: border-color 0.2s; }
.search-box:focus-within { border-color: rgba(124, 58, 237, 0.5); }
.search-box svg { width: 18px; height: 18px; color: #52525b; }
.search-input { background: none; border: none; outline: none; color: #fff; font-size: 14px; width: 100%; }

.genre-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 16px; }
.skeleton-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 16px; height: 120px; animation: pulse 1.5s infinite; }

.genre-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 20px; padding: 20px; transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1); display: flex; flex-direction: column; gap: 12px; }
.genre-card:hover { transform: translateY(-4px); border-color: rgba(124, 58, 237, 0.4); background: #1c1c1f; box-shadow: 0 12px 24px rgba(0,0,0,0.2); }

.genre-card-header { display: flex; justify-content: space-between; align-items: flex-start; }
.genre-name { font-size: 16px; font-weight: 800; color: #fff; letter-spacing: -0.01em; }
.genre-slug { font-size: 12px; color: #7c3aed; font-weight: 600; font-family: 'JetBrains Mono', monospace; opacity: 0.8; }
.genre-desc { font-size: 13px; color: #a1a1aa; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.genre-desc.empty { font-style: italic; opacity: 0.5; }

.genre-actions-top { display: flex; gap: 4px; opacity: 0; transition: opacity 0.2s; }
.genre-card:hover .genre-actions-top { opacity: 1; }
.icon-btn { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.05); color: #a1a1aa; cursor: pointer; transition: all 0.2s; }
.icon-btn:hover { background: rgba(124, 58, 237, 0.1); color: #a78bfa; border-color: rgba(124, 58, 237, 0.2); }
.icon-btn.delete:hover { background: rgba(239, 68, 68, 0.1); color: #f87171; border-color: rgba(239, 68, 68, 0.2); }
.icon-btn svg { width: 16px; height: 16px; }

.empty-state { grid-column: 1/-1; padding: 60px 20px; text-align: center; background: rgba(255,255,255,0.02); border: 2px dashed rgba(255,255,255,0.05); border-radius: 32px; display: flex; flex-direction: column; align-items: center; gap: 12px; }
.empty-icon { width: 64px; height: 64px; border-radius: 20px; background: rgba(255,255,255,0.03); display: flex; align-items: center; justify-content: center; color: #52525b; margin-bottom: 8px; }
.empty-icon svg { width: 32px; height: 32px; }
.empty-state h3 { font-size: 18px; font-weight: 800; color: #fff; margin: 0; }
.empty-state p { font-size: 14px; color: #71717a; max-width: 300px; margin: 0 0 8px; }

/* Modal Premium */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.8); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 20px; }
.modal { background: #18181b; border: 1px solid rgba(255,255,255,0.1); border-radius: 24px; width: 100%; max-width: 480px; overflow: hidden; box-shadow: 0 24px 48px rgba(0,0,0,0.4); animation: modal-in 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
@keyframes modal-in { from { opacity: 0; transform: translateY(20px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
.modal-header { padding: 24px 28px; border-bottom: 1px solid rgba(255,255,255,0.05); display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { font-size: 20px; font-weight: 800; color: #fff; margin: 0; }
.modal-close { background: none; border: none; color: #52525b; font-size: 20px; cursor: pointer; transition: color 0.2s; }
.modal-close:hover { color: #fff; }
.modal-body { padding: 28px; display: flex; flex-direction: column; gap: 20px; }
.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-group label { font-size: 13px; font-weight: 700; color: #a1a1aa; text-transform: uppercase; letter-spacing: 0.05em; }
.form-input { background: #0f0f11; border: 1px solid rgba(255,255,255,0.08); border-radius: 12px; padding: 12px 16px; color: #fff; font-size: 14px; transition: all 0.2s; }
.form-input:focus { outline: none; border-color: #7c3aed; box-shadow: 0 0 0 4px rgba(124, 58, 237, 0.1); }
.modal-footer { padding: 24px 28px; background: rgba(255,255,255,0.02); display: flex; justify-content: flex-end; gap: 12px; }

.pagination { display: flex; justify-content: center; gap: 8px; margin-top: 32px; }
.page-btn { padding: 10px 18px; border-radius: 12px; font-size: 14px; font-weight: 600; border: 1px solid rgba(255,255,255,0.1); background: #18181b; color: #a1a1aa; cursor: pointer; transition: all 0.2s; }
.page-btn:hover:not(:disabled) { background: #27272a; color: #fff; border-color: rgba(255,255,255,0.2); }
.page-btn.active { background: #7c3aed; color: white; border-color: #7c3aed; box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3); }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
</style>
