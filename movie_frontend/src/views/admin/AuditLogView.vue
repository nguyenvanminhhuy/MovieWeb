<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'

const logs = ref<any[]>([])
const loading = ref(true)
const page = ref(1)
const totalPages = ref(1)

async function fetchLogs() {
  loading.value = true
  try {
    const res = await adminApi.getAuditLogs(page.value, 50)
    logs.value = res.data
    totalPages.value = res.totalPages
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getActionColor(action: string) {
  if (action?.includes('CREATE')) return 'success'
  if (action?.includes('DELETE')) return 'danger'
  if (action?.includes('UPDATE')) return 'warning'
  return 'info'
}

onMounted(fetchLogs)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Audit Logs</h1>
          <p class="page-subtitle">Nhật ký hoạt động của hệ thống</p>
        </div>
        <button @click="fetchLogs" class="btn btn-secondary">Làm mới</button>
      </div>

      <div class="table-card mt-6">
        <table class="data-table">
          <thead>
            <tr>
              <th>Thời gian</th>
              <th>Người dùng</th>
              <th>Hành động</th>
              <th>Đối tượng</th>
              <th>Thông tin</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading" v-for="i in 10" :key="i">
              <td colspan="5"><div class="skeleton h-8 w-full"></div></td>
            </tr>
            <tr v-else v-for="log in logs" :key="log.id" class="data-row">
              <td class="text-xs text-zinc-500 font-mono">
                {{ log.timestamp ? new Date(log.timestamp).toLocaleString('vi-VN') : '—' }}
              </td>
              <td>
                <div class="font-medium text-zinc-300">{{ log.username || 'System' }}</div>
              </td>
              <td>
                <span :class="['action-badge', getActionColor(log.action)]">
                  {{ log.action }}
                </span>
              </td>
              <td class="text-sm text-zinc-400">{{ log.entityType }}</td>
              <td class="text-xs text-zinc-500 max-w-xs truncate" :title="log.details">
                {{ log.details }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 20px; }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; }
.page-title { font-size: 24px; font-weight: 800; color: #f4f4f5; margin: 0; }
.page-subtitle { font-size: 13px; color: #71717a; }

.table-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 16px; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { padding: 12px 16px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; color: #52525b; text-align: left; background: rgba(255,255,255,0.02); }
.data-table td { padding: 12px 16px; border-bottom: 1px solid rgba(255,255,255,0.04); }

.action-badge { font-size: 10px; font-weight: 700; padding: 2px 6px; border-radius: 4px; text-transform: uppercase; }
.action-badge.success { background: rgba(34,197,94,0.1); color: #4ade80; }
.action-badge.danger { background: rgba(239,68,68,0.1); color: #f87171; }
.action-badge.warning { background: rgba(245,158,11,0.1); color: #fbbf24; }
.action-badge.info { background: rgba(59,130,246,0.1); color: #60a5fa; }

.skeleton { background: rgba(255,255,255,0.05); border-radius: 4px; animation: pulse 1.5s infinite; }
@keyframes pulse { 0%,100% { opacity:1 } 50% { opacity:0.4 } }

.btn-secondary { background: #27272a; color: #e4e4e7; border: 1px solid rgba(255,255,255,0.08); padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
</style>
