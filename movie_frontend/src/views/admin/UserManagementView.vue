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

onMounted(fetchUsers)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Người dùng</h1>
          <p class="page-subtitle">{{ totalElements.toLocaleString() }} tài khoản đã đăng ký</p>
        </div>
      </div>

      <div v-if="err" class="alert alert-error">
        <div class="dot"></div>
        {{ err }}
      </div>

      <!-- Filters -->
      <div class="filters-row">
        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8" />
            <path d="M21 21l-4.35-4.35" stroke-linecap="round" />
          </svg>
          <input v-model="search" placeholder="Tìm theo username, email..." class="search-input" />
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
            Bị khóa
          </button>
        </div>
      </div>

      <!-- Table -->
      <div class="table-card">
        <table class="data-table">
          <thead>
            <tr>
              <th>Tài khoản</th>
              <th>Email</th>
              <th>Quyền hạn</th>
              <th>Ngày tạo</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading" v-for="i in 8" :key="i">
              <td>
                <div class="flex items-center gap-3">
                  <div class="skeleton" style="width: 38px; height: 38px; border-radius: 50%"></div>
                  <div class="skeleton" style="width: 110px; height: 13px"></div>
                </div>
              </td>
              <td><div class="skeleton" style="width: 150px; height: 12px"></div></td>
              <td>
                <div class="skeleton" style="width: 60px; height: 18px; border-radius: 20px"></div>
              </td>
              <td><div class="skeleton" style="width: 80px; height: 12px"></div></td>
              <td>
                <div class="skeleton" style="width: 80px; height: 22px; border-radius: 20px"></div>
              </td>
              <td>
                <div class="skeleton" style="width: 90px; height: 28px; border-radius: 8px"></div>
              </td>
            </tr>
            <tr v-else v-for="u in getFilteredUsers()" :key="u.id" class="data-row">
              <td>
                <div class="user-cell">
                  <div class="user-avatar">
                    <img v-if="u.avatar" :src="u.avatar" alt="" />
                    <span v-else>{{ u.username.charAt(0).toUpperCase() }}</span>
                  </div>
                  <div>
                    <div class="user-name">{{ u.username }}</div>
                    <div class="user-id">{{ u.id.slice(0, 8) }}...</div>
                  </div>
                </div>
              </td>
              <td class="text-muted">{{ u.email || '—' }}</td>
              <td>
                <div class="roles">
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
                  {{ u.enabled ? '● Hoạt động' : '● Bị khóa' }}
                </span>
              </td>
              <td>
                <div class="action-btns">
                  <button
                    @click="handleStatusToggle(u.id, u.enabled ?? true)"
                    :class="['action-btn', u.enabled ? 'warn' : 'success']"
                  >
                    {{ u.enabled ? 'Khóa' : 'Mở' }}
                  </button>
                  <button @click="handleDelete(u.id)" class="action-btn delete">Xóa</button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && getFilteredUsers().length === 0">
              <td colspan="6" class="empty-cell">
                <div class="empty-state">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                    <circle cx="9" cy="7" r="4" />
                  </svg>
                  <p>Không tìm thấy người dùng.</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button @click="changePage(Math.max(1, page - 1))" :disabled="page === 1" class="page-btn">‹</button>
        <button v-for="p in totalPages" :key="p" @click="changePage(p)" :class="['page-btn', page === p ? 'active' : '']">{{ p }}</button>
        <button @click="changePage(Math.min(totalPages, page + 1))" :disabled="page === totalPages" class="page-btn">›</button>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
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
}

.alert {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 16px;
  border-radius: 12px;
  font-size: 13px;
}
.alert-error {
  background: rgba(239, 68, 68, 0.08);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #f87171;
}
.dot {
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

.filters-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}
.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  padding: 9px 14px;
  flex: 1;
  min-width: 200px;
  max-width: 320px;
}
.search-box svg {
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
.filter-tabs {
  display: flex;
  gap: 4px;
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  padding: 4px;
}
.filter-tab {
  padding: 6px 14px;
  border-radius: 7px;
  font-size: 12px;
  font-weight: 500;
  border: none;
  background: none;
  color: #71717a;
  cursor: pointer;
  transition: all 0.15s;
}
.filter-tab:hover {
  color: #e4e4e7;
}
.filter-tab.active {
  background: #27272a;
  color: #e4e4e7;
}

.table-card {
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  overflow: hidden;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th {
  padding: 12px 16px;
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
.flex {
  display: flex;
}
.items-center {
  align-items: center;
}
.gap-3 {
  gap: 12px;
}
.skeleton {
  background: rgba(255, 255, 255, 0.07);
  border-radius: 4px;
  animation: pulse 1.5s infinite;
}
.text-muted {
  font-size: 13px;
  color: #71717a;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
  overflow: hidden;
}
.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.user-name {
  font-size: 13.5px;
  font-weight: 600;
  color: #e4e4e7;
}
.user-id {
  font-size: 11px;
  color: #52525b;
  font-family: monospace;
  margin-top: 2px;
}

.roles {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
.role-badge {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  padding: 3px 8px;
  border-radius: 20px;
}
.role-badge.admin {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
}
.role-badge.user {
  background: rgba(255, 255, 255, 0.06);
  color: #a1a1aa;
}

.status-pill {
  font-size: 11px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 20px;
  white-space: nowrap;
}
.status-pill.active {
  background: rgba(34, 197, 94, 0.1);
  color: #4ade80;
}
.status-pill.locked {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
}

.action-btns {
  display: flex;
  gap: 6px;
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
.action-btn.warn {
  background: rgba(245, 158, 11, 0.1);
  color: #fbbf24;
}
.action-btn.warn:hover {
  background: rgba(245, 158, 11, 0.2);
}
.action-btn.success {
  background: rgba(34, 197, 94, 0.1);
  color: #4ade80;
}
.action-btn.success:hover {
  background: rgba(34, 197, 94, 0.2);
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
  padding: 48px;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #52525b;
}
.empty-state svg {
  width: 36px;
  height: 36px;
}
.empty-state p {
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
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
</style>
