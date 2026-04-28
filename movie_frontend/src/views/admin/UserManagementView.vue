<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import type { UserResponse } from '../../api/types'

const users = ref<UserResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')
const filter = ref<'all' | 'active' | 'locked'>('all')

const showEditModal = ref(false)
const editingUser = ref<UserResponse | null>(null)
const formUsername = ref('')
const formEmail = ref('')
const formRoles = ref<string[]>([])
const saving = ref(false)

const allRoles = ref<any[]>([])

async function fetchRoles() {
  try {
    allRoles.value = await adminApi.getAllRoles()
  } catch (e) {
    console.error('Lỗi tải roles:', e)
  }
}

async function fetchUsers() {
  loading.value = true
  err.value = ''
  try {
    const res = await adminApi.getAllUsers(page.value, 15)
    users.value = res.data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải danh sách người dùng'
  } finally {
    loading.value = false
  }
}

function openEdit(u: UserResponse) {
  editingUser.value = u
  formUsername.value = u.username
  formEmail.value = u.email || ''
  formRoles.value = (u.roles || []).map((r) => r.name)
  showEditModal.value = true
}

async function handleSaveUser() {
  if (!editingUser.value) return
  saving.value = true
  try {
    const payload = {
      username: formUsername.value,
      email: formEmail.value,
      roles: formRoles.value.map((name) => ({ name })),
    }
    await adminApi.updateUser(editingUser.value.id, payload)
    showEditModal.value = false
    await fetchUsers()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi cập nhật người dùng')
  } finally {
    saving.value = false
  }
}

async function handleStatusToggle(id: string, currentEnabled: boolean) {
  try {
    await adminApi.updateUserStatus(id, !currentEnabled)
    await fetchUsers()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi cập nhật trạng thái')
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa người dùng này?')) return
  try {
    await adminApi.deleteUser(id)
    await fetchUsers()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa')
  }
}

function getFilteredUsers() {
  return users.value.filter((u) => {
    const matchSearch =
      !search.value ||
      u.username.toLowerCase().includes(search.value.toLowerCase()) ||
      (u.email || '').toLowerCase().includes(search.value.toLowerCase())
    const matchFilter =
      filter.value === 'all' ||
      (filter.value === 'active' && u.enabled) ||
      (filter.value === 'locked' && !u.enabled)
    return matchSearch && matchFilter
  })
}

function changePage(p: number) {
  page.value = p
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

<template>
  <AdminLayout>
    <div class="page">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Quản lý Tài khoản</h1>
          <p class="page-subtitle">{{ totalElements.toLocaleString() }} người dùng đã tham gia hệ thống</p>
        </div>
      </div>

      <div v-if="err" class="alert alert-error">
        <div class="dot"></div>
        {{ err }}
      </div>

      <!-- Filters & Actions -->
      <div class="filters-row">
        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8" />
            <path d="M21 21l-4.35-4.35" stroke-linecap="round" />
          </svg>
          <input v-model="search" placeholder="Tìm kiếm tài khoản..." class="search-input" />
        </div>
        <div class="filter-tabs">
          <button :class="['filter-tab', filter === 'all' ? 'active' : '']" @click="filter = 'all'">
            Tất cả
          </button>
          <button
            :class="['filter-tab', filter === 'active' ? 'active' : '']"
            @click="filter = 'active'"
          >
            Hoạt động
          </button>
          <button
            :class="['filter-tab', filter === 'locked' ? 'active' : '']"
            @click="filter = 'locked'"
          >
            Đang khóa
          </button>
        </div>
      </div>

      <!-- Table Section -->
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Người dùng</th>
              <th>Email</th>
              <th>Vai trò</th>
              <th>Ngày tham gia</th>
              <th>Trạng thái</th>
              <th class="text-right">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading" v-for="i in 8" :key="i">
              <td>
                <div class="user-cell">
                  <div class="skeleton avatar"></div>
                  <div class="skeleton text-sm" style="width: 100px"></div>
                </div>
              </td>
              <td><div class="skeleton text-xs" style="width: 140px"></div></td>
              <td><div class="skeleton badge"></div></td>
              <td><div class="skeleton text-xs" style="width: 80px"></div></td>
              <td><div class="skeleton pill"></div></td>
              <td><div class="skeleton btn-row"></div></td>
            </tr>
            <tr v-else v-for="u in getFilteredUsers()" :key="u.id" class="data-row">
              <td>
                <div class="user-cell">
                  <div class="user-avatar" :style="!u.avatar ? `background: ${getRandomColor(u.username)}` : ''">
                    <img v-if="u.avatar" :src="u.avatar" alt="" />
                    <span v-else>{{ u.username.charAt(0).toUpperCase() }}</span>
                  </div>
                  <div>
                    <div class="user-name">{{ u.username }}</div>
                    <div class="user-meta-id">ID: {{ u.id.slice(0, 8) }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span class="email-text">{{ u.email || '—' }}</span>
              </td>
              <td>
                <div class="roles-wrap">
                  <span
                    v-for="r in u.roles"
                    :key="r.name"
                    :class="['role-badge', r.name === 'ADMIN' ? 'admin' : 'user']"
                  >
                    {{ r.name }}
                  </span>
                </div>
              </td>
              <td class="text-muted">
                {{ u.createdAt ? new Date(u.createdAt).toLocaleDateString('vi-VN') : '—' }}
              </td>
              <td>
                <span :class="['status-pill', u.enabled ? 'active' : 'locked']">
                  {{ u.enabled ? 'Đang hoạt động' : 'Tài khoản bị khóa' }}
                </span>
              </td>
              <td>
                <div class="action-btns-wrap text-right">
                  <button @click="openEdit(u)" class="btn-icon edit" title="Chỉnh sửa">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                  </button>
                  <button
                    @click="handleStatusToggle(u.id, u.enabled ?? true)"
                    :class="['btn-icon', u.enabled ? 'lock' : 'unlock']"
                    :title="u.enabled ? 'Khóa tài khoản' : 'Mở khóa'"
                  >
                    <svg v-if="u.enabled" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
                    <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 019.9-1"/></svg>
                  </button>
                  <button @click="handleDelete(u.id)" class="btn-icon delete" title="Xóa vĩnh viễn">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && getFilteredUsers().length === 0">
              <td colspan="6" class="empty-row">
                <div class="empty-state-large">
                  <div class="empty-icon">👥</div>
                  <h3>Không có người dùng</h3>
                  <p>Không tìm thấy kết quả phù hợp với tiêu chí tìm kiếm của bạn.</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-wrap">
        <button @click="changePage(Math.max(1, page - 1))" :disabled="page === 1" class="p-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
        </button>
        <div class="p-pages">
          <button v-for="p in totalPages" :key="p" @click="changePage(p)" :class="['p-page', page === p ? 'active' : '']">{{ p }}</button>
        </div>
        <button @click="changePage(Math.min(totalPages, page + 1))" :disabled="page === totalPages" class="p-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
        </button>
      </div>

      <!-- User Edit Modal -->
      <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
        <div class="modal-card">
          <div class="modal-header">
            <h3>Chỉnh sửa tài khoản</h3>
            <button @click="showEditModal = false" class="close-modal">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-grid">
              <div class="form-item">
                <label>Tên đăng nhập</label>
                <input v-model="formUsername" type="text" placeholder="Username" />
              </div>
              <div class="form-item">
                <label>Địa chỉ Email</label>
                <input v-model="formEmail" type="email" placeholder="example@email.com" />
              </div>
              <div class="form-item span-full">
                <label>Quyền hạn tài khoản</label>
                <div class="role-selector">
                  <label v-for="r in allRoles" :key="r.name" class="role-checkbox">
                    <input type="checkbox" :value="r.name" v-model="formRoles" />
                    <span>{{ r.name }}</span>
                  </label>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button @click="showEditModal = false" class="btn-cancel">Hủy bỏ</button>
            <button @click="handleSaveUser" :disabled="saving" class="btn-save">
              {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script lang="ts">
function getRandomColor(str: string) {
  const colors = ['#7c3aed', '#db2777', '#2563eb', '#059669', '#d97706', '#dc2626']
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
}
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 28px; }
.page-title { font-size: 32px; font-weight: 900; color: #fff; margin: 0; letter-spacing: -0.04em; }
.page-subtitle { font-size: 15px; color: #71717a; margin-top: 6px; }

/* Filters */
.filters-row { display: flex; align-items: center; justify-content: space-between; gap: 20px; flex-wrap: wrap; }
.search-box { display: flex; align-items: center; gap: 12px; background: #18181b; border: 1px solid rgba(255,255,255,0.08); border-radius: 16px; padding: 12px 20px; width: 100%; max-width: 440px; transition: all 0.2s; }
.search-box:focus-within { border-color: #7c3aed; box-shadow: 0 0 0 4px rgba(124, 58, 237, 0.1); }
.search-box svg { width: 18px; height: 18px; color: #52525b; }
.search-input { background: none; border: none; outline: none; color: #fff; font-size: 14px; width: 100%; }

.filter-tabs { display: flex; background: #18181b; padding: 4px; border-radius: 14px; border: 1px solid rgba(255,255,255,0.08); }
.filter-tab { padding: 8px 16px; border-radius: 10px; font-size: 13.5px; font-weight: 700; color: #71717a; border: none; background: none; cursor: pointer; transition: all 0.2s; }
.filter-tab.active { background: #27272a; color: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.2); }

/* Table Container */
.table-container { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 24px; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; text-align: left; }
.data-table th { padding: 20px 24px; font-size: 12px; font-weight: 800; color: #52525b; text-transform: uppercase; letter-spacing: 0.1em; border-bottom: 1px solid rgba(255,255,255,0.04); }
.data-table td { padding: 16px 24px; font-size: 14px; border-bottom: 1px solid rgba(255,255,255,0.03); vertical-align: middle; }
.data-row:hover { background: rgba(255,255,255,0.01); }

/* User Cell */
.user-cell { display: flex; align-items: center; gap: 14px; }
.user-avatar { width: 44px; height: 44px; border-radius: 14px; overflow: hidden; display: flex; align-items: center; justify-content: center; font-weight: 900; color: #fff; font-size: 18px; flex-shrink: 0; }
.user-avatar img { width: 100%; height: 100%; object-fit: cover; }
.user-name { font-weight: 800; color: #fff; margin-bottom: 2px; }
.user-meta-id { font-size: 11px; color: #52525b; font-family: monospace; }

.email-text { color: #a1a1aa; font-weight: 500; }

/* Role Badges */
.roles-wrap { display: flex; gap: 6px; flex-wrap: wrap; }
.role-badge { padding: 4px 10px; border-radius: 8px; font-size: 11px; font-weight: 800; letter-spacing: 0.02em; }
.role-badge.admin { background: rgba(124, 58, 237, 0.1); color: #a78bfa; border: 1px solid rgba(124, 58, 237, 0.2); }
.role-badge.user { background: rgba(255, 255, 255, 0.05); color: #a1a1aa; border: 1px solid rgba(255, 255, 255, 0.05); }

/* Status Pill */
.status-pill { padding: 6px 12px; border-radius: 20px; font-size: 12px; font-weight: 700; display: inline-flex; align-items: center; gap: 6px; }
.status-pill::before { content: ''; width: 6px; height: 6px; border-radius: 50%; }
.status-pill.active { background: rgba(16, 185, 129, 0.1); color: #34d399; }
.status-pill.active::before { background: #10b981; }
.status-pill.locked { background: rgba(239, 68, 68, 0.1); color: #f87171; }
.status-pill.locked::before { background: #ef4444; }

/* Action Buttons */
.action-btns-wrap { display: flex; gap: 8px; justify-content: flex-end; }
.btn-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; border: none; cursor: pointer; transition: all 0.2s; background: rgba(255,255,255,0.04); color: #71717a; }
.btn-icon svg { width: 18px; height: 18px; }
.btn-icon:hover { transform: scale(1.1); background: rgba(255,255,255,0.08); color: #fff; }
.btn-icon.edit:hover { background: rgba(124, 58, 237, 0.15); color: #a78bfa; }
.btn-icon.lock:hover { background: rgba(234, 179, 8, 0.15); color: #facc15; }
.btn-icon.unlock:hover { background: rgba(16, 185, 129, 0.15); color: #34d399; }
.btn-icon.delete:hover { background: rgba(239, 68, 68, 0.15); color: #f87171; }

/* Pagination */
.pagination-wrap { display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 32px; }
.p-pages { display: flex; gap: 8px; }
.p-page { width: 40px; height: 40px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; border: 1px solid rgba(255,255,255,0.08); background: #18181b; color: #71717a; cursor: pointer; transition: all 0.2s; }
.p-page.active { background: #7c3aed; color: #fff; border-color: #7c3aed; box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3); }
.p-btn { width: 40px; height: 40px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.08); background: #18181b; color: #71717a; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.p-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.p-btn svg { width: 20px; height: 20px; }

/* Modal Premium */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.85); backdrop-filter: blur(12px); display: flex; align-items: center; justify-content: center; z-index: 2000; padding: 20px; }
.modal-card { background: #18181b; border: 1px solid rgba(255,255,255,0.1); border-radius: 32px; width: 100%; max-width: 540px; overflow: hidden; box-shadow: 0 32px 64px rgba(0,0,0,0.5); animation: modal-reveal 0.4s cubic-bezier(0.4, 0, 0.2, 1); }
@keyframes modal-reveal { from { opacity: 0; transform: translateY(30px) scale(0.95); } to { opacity: 1; transform: translateY(0) scale(1); } }
.modal-header { padding: 32px 32px 24px; display: flex; justify-content: space-between; align-items: center; }
.modal-header h3 { font-size: 24px; font-weight: 900; color: #fff; margin: 0; letter-spacing: -0.02em; }
.close-modal { background: none; border: none; color: #52525b; font-size: 24px; cursor: pointer; transition: color 0.2s; }
.close-modal:hover { color: #fff; }
.modal-body { padding: 0 32px 32px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.form-item { display: flex; flex-direction: column; gap: 10px; }
.form-item.span-full { grid-column: span 2; }
.form-item label { font-size: 13px; font-weight: 800; color: #52525b; text-transform: uppercase; letter-spacing: 0.05em; }
.form-item input { background: #0f0f11; border: 1px solid rgba(255,255,255,0.08); border-radius: 14px; padding: 14px 18px; color: #fff; font-size: 15px; transition: all 0.2s; }
.form-item input:focus { outline: none; border-color: #7c3aed; box-shadow: 0 0 0 4px rgba(124, 58, 237, 0.1); }

/* Role Selector */
.role-selector { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 4px; }
.role-checkbox { position: relative; cursor: pointer; }
.role-checkbox input { display: none; }
.role-checkbox span { display: block; padding: 10px 20px; border-radius: 14px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.08); color: #71717a; font-size: 13px; font-weight: 700; transition: all 0.2s; }
.role-checkbox input:checked + span { background: rgba(124, 58, 237, 0.1); border-color: #7c3aed; color: #a78bfa; }

.modal-footer { padding: 24px 32px; background: rgba(255,255,255,0.02); display: flex; justify-content: flex-end; gap: 14px; }
.btn-cancel { padding: 12px 24px; border-radius: 14px; background: none; border: 1px solid rgba(255,255,255,0.1); color: #a1a1aa; font-weight: 700; cursor: pointer; transition: all 0.2s; }
.btn-cancel:hover { background: rgba(255,255,255,0.05); color: #fff; }
.btn-save { padding: 12px 28px; border-radius: 14px; background: #7c3aed; border: none; color: #fff; font-weight: 800; cursor: pointer; transition: all 0.2s; box-shadow: 0 8px 20px rgba(124, 58, 237, 0.3); }
.btn-save:hover { background: #6d28d9; transform: translateY(-2px); box-shadow: 0 12px 24px rgba(124, 58, 237, 0.4); }
.btn-save:disabled { opacity: 0.5; transform: none; box-shadow: none; }

/* Skeleton */
.skeleton { background: linear-gradient(90deg, #18181b 25%, #27272a 50%, #18181b 75%); background-size: 200% 100%; animation: skeleton-shimmer 1.5s infinite; border-radius: 4px; }
@keyframes skeleton-shimmer { from { background-position: 200% 0; } to { background-position: -200% 0; } }
.skeleton.avatar { width: 44px; height: 44px; border-radius: 14px; }
.skeleton.badge { width: 60px; height: 22px; border-radius: 8px; }
.skeleton.pill { width: 100px; height: 24px; border-radius: 20px; }
.skeleton.btn-row { width: 120px; height: 36px; border-radius: 10px; margin-left: auto; }

/* Empty States */
.empty-row { padding: 80px 0 !important; }
.empty-state-large { display: flex; flex-direction: column; align-items: center; gap: 16px; color: #52525b; text-align: center; }
.empty-icon { font-size: 48px; opacity: 0.5; }
.empty-state-large h3 { font-size: 20px; font-weight: 800; color: #fff; margin: 0; }
.empty-state-large p { font-size: 14px; max-width: 320px; margin: 0; line-height: 1.6; }

.text-right { text-align: right; }
</style>
