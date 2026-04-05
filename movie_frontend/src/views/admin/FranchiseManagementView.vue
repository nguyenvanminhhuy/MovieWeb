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

      <div v-else class="item-grid">
        <div
          v-for="f in getFiltered()"
          :key="f.id"
          class="franchise-card"
        >
          <div class="franchise-poster" :style="f.coverUrl ? `background-image: url(${f.coverUrl})` : ''">
            <div v-if="!f.coverUrl" class="franchise-initial">{{ f.name.charAt(0).toUpperCase() }}</div>
          </div>
          <div class="franchise-info">
            <div class="franchise-name">{{ f.name }}</div>
            <div v-if="f.description" class="franchise-desc">{{ f.description }}</div>
            <div class="card-actions">
              <button @click="openEdit(f)" class="action-btn edit">Sửa</button>
              <button @click="handleDelete(f.id)" class="action-btn delete">Xóa</button>
            </div>
          </div>
        </div>
        
        <!-- Empty State -->
        <div v-if="!loading && getFiltered().length === 0" class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10" /><path d="M12 8v4l3 3" />
          </svg>
          <p>Không tìm thấy franchise nào.</p>
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
.page { display: flex; flex-direction: column; gap: 20px; }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; flex-wrap: wrap; gap: 12px; }
.page-title { font-size: 24px; font-weight: 800; color: #f4f4f5; margin: 0; }
.page-subtitle { font-size: 13px; color: #71717a; margin: 4px 0 0; }
.btn { display: inline-flex; align-items: center; gap: 7px; padding: 9px 16px; border-radius: 10px; font-size: 13.5px; font-weight: 600; border: none; cursor: pointer; transition: all 0.15s; }
.btn svg { width: 15px; height: 15px; }
.btn:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-primary { background: #7c3aed; color: white; }
.btn-primary:not(:disabled):hover { background: #6d28d9; transform: translateY(-1px); }
.btn-ghost { background: transparent; color: #71717a; }
.btn-ghost:hover { background: rgba(255,255,255,0.05); color: #f4f4f5; }
.alert { padding: 11px 16px; border-radius: 12px; font-size: 13px; }
.alert-error { background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.2); color: #f87171; }
.search-box { display: flex; align-items: center; gap: 8px; background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; padding: 9px 14px; max-width: 320px; }
.search-box svg { width: 15px; height: 15px; color: #52525b; flex-shrink: 0; }
.search-input { background: none; border: none; outline: none; color: #e4e4e7; font-size: 13px; width: 100%; }
.search-input::placeholder { color: #52525b; }

.item-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 14px; }
.skeleton-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; height: 110px; animation: pulse 1.5s infinite; }
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }

.franchise-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; overflow: hidden; transition: all 0.15s; }
.franchise-card:hover { border-color: rgba(255,255,255,0.1); transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.3); }
.franchise-poster {
  height: 60px;
  background: linear-gradient(135deg, #1e1b4b, #2e1065);
  background-size: cover; background-position: center;
  display: flex; align-items: center; justify-content: center;
}
.franchise-initial { font-size: 24px; font-weight: 800; color: rgba(167,139,250,0.5); }
.franchise-info { padding: 12px; }
.franchise-name { font-size: 13.5px; font-weight: 700; color: #e4e4e7; }
.franchise-desc { font-size: 11.5px; color: #71717a; margin-top: 4px; line-clamp: 1; -webkit-line-clamp: 1; -webkit-box-orient: vertical; display: -webkit-box; overflow: hidden; }

.card-actions { display: flex; gap: 8px; margin-top: 10px; padding-top: 8px; border-top: 1px solid rgba(255,255,255,0.04); }
.action-btn { padding: 4px 10px; border-radius: 6px; font-size: 11px; font-weight: 600; border: none; cursor: pointer; transition: all 0.13s; }
.action-btn.edit { background: rgba(124,58,237,0.1); color: #a78bfa; }
.action-btn.edit:hover { background: rgba(124,58,237,0.2); }
.action-btn.delete { background: rgba(239,68,68,0.08); color: #f87171; }
.action-btn.delete:hover { background: rgba(239,68,68,0.15); }

.empty-state {
  grid-column: 1/-1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px dashed rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  color: #71717a;
}
.empty-state svg { width: 48px; height: 48px; margin-bottom: 16px; opacity: 0.3; }
.empty-state p { margin: 0; font-size: 14px; font-weight: 500; }

/* Pagination */
.pagination { display: flex; justify-content: center; gap: 6px; margin-top: 20px; }
.page-btn { padding: 7px 13px; border-radius: 9px; font-size: 13px; font-weight: 500; border: 1px solid rgba(255, 255, 255, 0.06); background: #18181b; color: #71717a; cursor: pointer; transition: all 0.15s; }
.page-btn:hover:not(:disabled) { background: #27272a; color: #e4e4e7; }
.page-btn.active { background: #7c3aed; color: white; border-color: #7c3aed; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
</style>
