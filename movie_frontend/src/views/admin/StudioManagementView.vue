<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import type { StudioResponse } from '../../api/types'

const studios = ref<StudioResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')
const showModal = ref(false)
const editing = ref<StudioResponse | null>(null)
const formName = ref('')
const formDesc = ref('')
const formLogo = ref('')
const saving = ref(false)

function getFiltered() {
  return studios.value.filter(s => !search.value || s.name.toLowerCase().includes(search.value.toLowerCase()))
}

async function fetchStudios() {
  loading.value = true
  err.value = ''
  try {
    const res = await adminApi.getAllStudiosAdmin(page.value, 15)
    studios.value = res.data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải studio'
  } finally {
    loading.value = false
  }
}

function changePage(p: number) {
  page.value = p
  fetchStudios()
}

function openAdd() {
  editing.value = null
  formName.value = ''
  formDesc.value = ''
  formLogo.value = ''
  showModal.value = true
}

function openEdit(s: StudioResponse) {
  editing.value = s
  formName.value = s.name
  formDesc.value = s.description || ''
  formLogo.value = s.logoUrl || ''
  showModal.value = true
}

async function handleSave() {
  if (!formName.value.trim()) return
  saving.value = true
  try {
    const payload = { name: formName.value, description: formDesc.value, logoUrl: formLogo.value }
    if (editing.value?.id) {
      await adminApi.updateStudio(editing.value.id, payload)
    } else {
      await adminApi.createStudio(payload)
    }
    showModal.value = false
    await fetchStudios()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi lưu studio')
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa studio này?')) return
  try {
    await adminApi.deleteStudio(id)
    await fetchStudios()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa')
  }
}

onMounted(fetchStudios)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Studio</h1>
          <p class="page-subtitle">{{ totalElements.toLocaleString() }} studio trong hệ thống</p>
        </div>
        <button @click="openAdd" class="btn btn-primary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19" stroke-linecap="round"/>
            <line x1="5" y1="12" x2="19" y2="12" stroke-linecap="round"/>
          </svg>
          Thêm studio
        </button>
      </div>

      <div v-if="err" class="alert alert-error">{{ err }}</div>

      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35" stroke-linecap="round"/>
        </svg>
        <input v-model="search" placeholder="Tìm studio..." class="search-input" />
      </div>

      <div v-if="loading" class="item-grid">
        <div v-for="i in 8" :key="i" class="skeleton-card"></div>
      </div>

      <div v-else class="item-grid">
        <div
          v-for="s in getFiltered()"
          :key="s.id"
          class="studio-card"
        >
          <div class="studio-logo">
            <img v-if="s.logoUrl" :src="s.logoUrl" :alt="s.name" />
            <div v-else class="studio-initials">{{ s.name.charAt(0).toUpperCase() }}</div>
          </div>
          <div class="studio-info">
            <div class="studio-name">{{ s.name }}</div>
            <div v-if="s.description" class="studio-desc">{{ s.description }}</div>
            <div class="card-actions">
              <button @click="openEdit(s)" class="action-btn edit">Sửa</button>
              <button @click="handleDelete(s.id)" class="action-btn delete">Xóa</button>
            </div>
          </div>
        </div>
        
        <!-- Empty State -->
        <div v-if="!loading && getFiltered().length === 0" class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" /><polyline points="9 22 9 12 15 12 15 22" />
          </svg>
          <p>Không tìm thấy studio nào.</p>
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
            <h3>{{ editing ? 'Sửa studio' : 'Thêm studio mới' }}</h3>
            <button @click="showModal = false" class="modal-close">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Tên studio *</label>
              <input v-model="formName" class="form-input" placeholder="Ví dụ: Bones, Mappa..." />
            </div>
            <div class="form-group">
              <label>Logo URL</label>
              <input v-model="formLogo" class="form-input" placeholder="https://..." />
            </div>
            <div class="form-group">
              <label>Mô tả</label>
              <textarea v-model="formDesc" class="form-input" rows="3" placeholder="Giới thiệu về studio..."></textarea>
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

.item-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 12px; }
.skeleton-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; height: 80px; animation: pulse 1.5s infinite; }
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }

.studio-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; padding: 16px; display: flex; align-items: center; gap: 14px; transition: all 0.15s; }
.studio-card:hover { background: #1c1c1f; border-color: rgba(255,255,255,0.1); }
.studio-logo { width: 48px; height: 48px; border-radius: 10px; overflow: hidden; background: rgba(124,58,237,0.15); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.studio-logo img { width: 100%; height: 100%; object-fit: contain; }
.studio-initials { font-size: 18px; font-weight: 800; color: #a78bfa; }
.studio-name { font-size: 14px; font-weight: 700; color: #e4e4e7; }
.studio-desc { font-size: 12px; color: #71717a; margin-top: 4px; line-clamp: 1; -webkit-line-clamp: 1; -webkit-box-orient: vertical; display: -webkit-box; overflow: hidden; }

.card-actions { display: flex; gap: 8px; margin-top: 8px; }
.action-btn { padding: 4px 8px; border-radius: 6px; font-size: 11px; font-weight: 600; border: none; cursor: pointer; transition: all 0.13s; }
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
