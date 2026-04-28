<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'

const reports = ref<any[]>([])
const loading = ref(true)
const page = ref(1)
const totalPages = ref(1)

async function fetchReports() {
  loading.value = true
  try {
    const res = await adminApi.getAllReportsAdmin(page.value, 20)
    reports.value = res.data
    totalPages.value = res.totalPages
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleResolve(id: string) {
  try {
    await adminApi.resolveReport(id)
    await fetchReports()
  } catch (e) {
    alert('Lỗi: ' + e)
  }
}

function changePage(p: number) {
  page.value = p
  fetchReports()
}

onMounted(fetchReports)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Quản lý báo cáo</h1>
          <p class="page-subtitle">Báo cáo vi phạm từ người dùng</p>
        </div>
      </div>

      <div class="table-card mt-6">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Ngày gửi</th>
              <th>Phim</th>
              <th>Lý do</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading" v-for="i in 5" :key="i">
              <td colspan="6"><div class="skeleton h-10 w-full"></div></td>
            </tr>
            <tr v-else v-for="r in reports" :key="r.id" class="data-row">
              <td class="font-mono text-xs text-zinc-500">#{{ r.id?.substring(0, 8) }}</td>
              <td class="text-xs">
                {{ r.createdAt ? new Date(r.createdAt).toLocaleString() : '—' }}
              </td>
              <td>
                <div class="text-sm font-medium">{{ r.movie?.title || `#${r.movieId}` }}</div>
                <div v-if="r.user" class="text-xs text-zinc-500">
                  Gửi bởi: {{ r.user.username }}
                </div>
              </td>
              <td>
                <div class="text-sm font-semibold text-rose-400">{{ r.reason }}</div>
                <div class="text-xs text-zinc-500 max-w-50 truncate" :title="r.description">
                  {{ r.description }}
                </div>
              </td>
              <td>
                <span :class="['status-badge', r.status === 'PENDING' ? 'pending' : 'resolved']">
                  {{ r.status === 'PENDING' ? 'Chưa xử lý' : 'Đã xử lý' }}
                </span>
              </td>
              <td>
                <button
                  v-if="r.status === 'PENDING'"
                  @click="handleResolve(r.id)"
                  class="action-btn"
                >
                  Đánh dấu xử lý
                </button>
              </td>
            </tr>
            <tr v-if="!loading && reports.length === 0">
              <td colspan="6" class="p-8 text-center text-zinc-500">Không có báo cáo nào.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button @click="changePage(Math.max(1, page - 1))" :disabled="page === 1" class="page-btn">
          ‹
        </button>
        <button
          v-for="p in totalPages"
          :key="p"
          @click="changePage(p)"
          :class="['page-btn', page === p ? 'active' : '']"
        >
          {{ p }}
        </button>
        <button
          @click="changePage(Math.min(totalPages, page + 1))"
          :disabled="page === totalPages"
          class="page-btn"
        >
          ›
        </button>
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
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: #f4f4f5;
  margin: 0;
}
.page-subtitle {
  font-size: 13px;
  color: #71717a;
  margin-top: 4px;
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
  padding: 14px 16px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  color: #52525b;
  text-align: left;
  background: rgba(255, 255, 255, 0.02);
}
.data-table td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.status-badge {
  font-size: 10px;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 20px;
  text-transform: uppercase;
}
.status-badge.pending {
  background: rgba(244, 63, 94, 0.1);
  color: #fb7185;
}
.status-badge.resolved {
  background: rgba(34, 197, 94, 0.1);
  color: #4ade80;
}

.action-btn {
  background: rgba(59, 130, 246, 0.1);
  color: #60a5fa;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.15s;
}
.action-btn:hover {
  background: rgba(59, 130, 246, 0.2);
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 10px;
}
.page-btn {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  color: #71717a;
  cursor: pointer;
}
.page-btn.active {
  background: #7c3aed;
  color: white;
  border-color: #7c3aed;
}

.skeleton {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  animation: pulse 1.5s infinite;
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
</style>
