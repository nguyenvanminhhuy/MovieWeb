<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'

const roles = ref<any[]>([])
const permissions = ref<any[]>([])
const loading = ref(true)

async function fetchData() {
  loading.value = true
  try {
    const [rl, pm] = await Promise.all([
      adminApi.getAllRoles(),
      adminApi.getAllPermissions()
    ])
    roles.value = rl
    permissions.value = pm
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleDeleteRole(name: string) {
  if (!confirm(`Xóa vai trò ${name}?`)) return
  try {
    await adminApi.deleteRole(name)
    await fetchData()
  } catch (e) {
    alert('Lỗi: ' + e)
  }
}

async function handleDeletePermission(name: string) {
  if (!confirm(`Xóa quyền hạn ${name}?`)) return
  try {
    await adminApi.deletePermission(name)
    await fetchData()
  } catch (e) {
    alert('Lỗi: ' + e)
  }
}

onMounted(fetchData)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Quyền & Vai trò</h1>
          <p class="page-subtitle">Quản lý hệ thống phân quyền (RBAC)</p>
        </div>
      </div>

      <div class="grid-layout">
        <section class="manage-section">
          <div class="section-top">
            <h2 class="section-title">Vai trò (Roles)</h2>
            <button class="add-btn">+</button>
          </div>
          <div class="list-card">
            <div v-if="loading" v-for="i in 3" :key="i" class="skeleton h-14 w-full mb-2"></div>
            <div v-else v-for="role in roles" :key="role.name" class="role-item">
              <div>
                <div class="item-name">{{ role.name }}</div>
                <div class="item-desc">{{ role.description || '—' }}</div>
              </div>
              <button @click="handleDeleteRole(role.name)" class="item-delete">×</button>
            </div>
          </div>
        </section>

        <section class="manage-section">
          <div class="section-top">
            <h2 class="section-title">Quyền hạn (Permissions)</h2>
            <button class="add-btn">+</button>
          </div>
          <div class="list-card">
            <div v-if="loading" v-for="i in 5" :key="i" class="skeleton h-14 w-full mb-2"></div>
            <div v-else v-for="pm in permissions" :key="pm.name" class="role-item">
              <div>
                <div class="item-name font-mono text-violet-400">{{ pm.name }}</div>
                <div class="item-desc">{{ pm.description || '—' }}</div>
              </div>
              <button @click="handleDeletePermission(pm.name)" class="item-delete">×</button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 24px; }
.page-title { font-size: 24px; font-weight: 800; color: #f4f4f5; margin: 0; }
.page-subtitle { font-size: 13px; color: #71717a; margin-top: 4px; }

.grid-layout { display: grid; grid-template-columns: 1fr 1.5fr; gap: 24px; }
@media (max-width: 992px) { .grid-layout { grid-template-columns: 1fr; } }

.manage-section { display: flex; flex-direction: column; gap: 16px; }
.section-top { display: flex; justify-content: space-between; align-items: center; }
.section-title { font-size: 16px; font-weight: 700; color: #e4e4e7; margin: 0; }

.add-btn { width: 24px; height: 24px; background: rgba(124,58,237,0.1); color: #a78bfa; border: 1px solid rgba(124,58,237,0.2); border-radius: 4px; display: flex; align-items: center; justify-content: center; font-weight: 700; cursor: pointer; }
.add-btn:hover { background: rgba(124,58,237,0.2); }

.list-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 16px; overflow: hidden; padding: 8px; }
.role-item { display: flex; align-items: center; justify-content: space-between; padding: 12px; border-radius: 10px; transition: 0.2s; border-bottom: 1px solid rgba(255,255,255,0.03); }
.role-item:last-child { border-bottom: none; }
.role-item:hover { background: rgba(255,255,255,0.03); }

.item-name { font-size: 14px; font-weight: 700; color: #f4f4f5; }
.item-desc { font-size: 12px; color: #71717a; margin-top: 2px; }
.item-delete { background: none; border: none; color: #52525b; font-size: 18px; cursor: pointer; width: 30px; height: 30px; border-radius: 50%; }
.item-delete:hover { background: rgba(239,68,68,0.1); color: #f87171; }

.skeleton { background: rgba(255,255,255,0.05); border-radius: 8px; animation: pulse 1.5s infinite; }
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }
</style>
