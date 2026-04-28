<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import type { FranchiseResponse } from '../../api/types'

const franchises = ref<FranchiseResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')
const showModal = ref(false)
const editing = ref<FranchiseResponse | null>(null)
const formName = ref('')
const formDesc = ref('')
const formPoster = ref('')
const saving = ref(false)

function getFiltered() {
  return franchises.value.filter(f => !search.value || f.name.toLowerCase().includes(search.value.toLowerCase()))
}

async function fetchFranchises() {
  loading.value = true
  err.value = ''
  try {
    const res = await adminApi.getAllFranchisesAdmin(page.value, 15)
    franchises.value = res.data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải franchise'
  } finally {
    loading.value = false
  }
}

function changePage(p: number) {
  page.value = p
  fetchFranchises()
}

function openAdd() {
  editing.value = null
  formName.value = ''
  formDesc.value = ''
  formPoster.value = ''
  showModal.value = true
}

function openEdit(f: FranchiseResponse) {
  editing.value = f
  formName.value = f.name
  formDesc.value = f.description || ''
  formPoster.value = f.coverUrl || ''
  showModal.value = true
}

async function handleSave() {
  if (!formName.value.trim()) return
  saving.value = true
  try {
    const payload = { name: formName.value, description: formDesc.value, coverUrl: formPoster.value }
    if (editing.value?.id) {
      await adminApi.updateFranchise(editing.value.id, payload)
    } else {
      await adminApi.createFranchise(payload)
    }
    showModal.value = false
    await fetchFranchises()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi lưu franchise')
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa franchise này?')) return
  try {
    await adminApi.deleteFranchise(id)
    await fetchFranchises()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa')
  }
}

onMounted(fetchFranchises)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Franchise</h1>
          <p class="page-subtitle">{{ totalElements.toLocaleString() }} franchise / series trong hệ thống</p>
        </div>
        <button @click="openAdd" class="btn btn-primary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19" stroke-linecap="round"/>
            <line x1="5" y1="12" x2="19" y2="12" stroke-linecap="round"/>
          </svg>
          Thêm franchise
        </button>
      </div>

      <div v-if="err" class="alert alert-error">{{ err }}</div>

      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35" stroke-linecap="round"/>
        </svg>
        <input v-model="search" placeholder="Tìm franchise..." class="search-input" />
      </div>

      <div v-if="loading" class="item-grid">
        <div v-for="i in 8" :key="i" class="skeleton-card"></div>
      </div>

      <!-- Grid -->
      <div v-else class="item-grid">
        <div v-for="f in getFiltered()" :key="f.id" class="franchise-card">
          <div class="franchise-poster" :style="f.coverUrl ? `background-image: url(${f.coverUrl})` : ''">
            <div v-if="!f.coverUrl" class="franchise-initial">{{ f.name.charAt(0).toUpperCase() }}</div>
            <div class="franchise-overlay">
              <div class="franchise-actions">
                <button @click="openEdit(f)" class="icon-btn edit" title="Sửa">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                </button>
                <button @click="handleDelete(f.id)" class="icon-btn delete" title="Xóa">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
                </button>
              </div>
            </div>
          </div>
          <div class="franchise-info">
            <div class="franchise-name">{{ f.name }}</div>
            <div v-if="f.description" class="franchise-desc">{{ f.description }}</div>
            <div v-else class="franchise-desc empty">Chưa có thông tin series.</div>
          </div>
        </div>
        
        <!-- Empty State -->
        <div v-if="!loading && getFiltered().length === 0" class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="12" r="10" /><path d="M12 8v4l3 3" />
            </svg>
          </div>
          <h3>Không tìm thấy franchise</h3>
          <p>Hiện chưa có series hoặc franchise nào được tạo.</p>
          <button @click="openAdd" class="btn btn-outline">Thêm franchise ngay</button>
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
            <h3>{{ editing ? 'Sửa franchise' : 'Thêm franchise mới' }}</h3>
            <button @click="showModal = false" class="modal-close">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Tên franchise *</label>
              <input v-model="formName" class="form-input" placeholder="Ví dụ: Dragon Ball, Naruto..." />
            </div>
            <div class="form-group">
              <label>Poster URL</label>
              <input v-model="formPoster" class="form-input" placeholder="https://..." />
            </div>
            <div class="form-group">
              <label>Mô tả</label>
              <textarea v-model="formDesc" class="form-input" rows="3" placeholder="Giới thiệu về franchise..."></textarea>
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
.search-box svg { width: 18px; height: 18px; color: #52525b; }
.search-input { background: none; border: none; outline: none; color: #fff; font-size: 14px; width: 100%; }

.item-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 20px; }
.skeleton-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 20px; height: 180px; animation: pulse 1.5s infinite; }

.franchise-card { background: #18181b; border: 1px solid rgba(255, 255, 255, 0.06); border-radius: 20px; overflow: hidden; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.franchise-card:hover { transform: translateY(-4px); border-color: rgba(124, 58, 237, 0.4); box-shadow: 0 12px 32px rgba(0,0,0,0.3); }

.franchise-poster { height: 120px; background: linear-gradient(135deg, #1e1b4b, #2e1065); background-size: cover; background-position: center; display: flex; align-items: center; justify-content: center; position: relative; }
.franchise-initial { font-size: 40px; font-weight: 900; color: rgba(124, 58, 237, 0.3); }
.franchise-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); opacity: 0; transition: opacity 0.2s; display: flex; align-items: center; justify-content: center; backdrop-filter: blur(4px); }
.franchise-card:hover .franchise-overlay { opacity: 1; }

.franchise-actions { display: flex; gap: 12px; }
.icon-btn { width: 40px; height: 40px; border-radius: 12px; display: flex; align-items: center; justify-content: center; background: #fff; color: #000; border: none; cursor: pointer; transition: all 0.2s; }
.icon-btn:hover { transform: scale(1.1); }
.icon-btn.delete { background: #f87171; color: #fff; }
.icon-btn svg { width: 18px; height: 18px; }

.franchise-info { padding: 16px; }
.franchise-name { font-size: 15px; font-weight: 800; color: #fff; margin-bottom: 4px; }
.franchise-desc { font-size: 12px; color: #71717a; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.franchise-desc.empty { font-style: italic; opacity: 0.5; }

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
