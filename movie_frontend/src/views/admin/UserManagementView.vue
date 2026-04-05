<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AppShell from '../../components/AppShell.vue'
import * as adminApi from '../../api/adminApi'
import type { UserResponse } from '../../api/types'

const users = ref<UserResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)

async function fetchUsers() {
  loading.value = true
  try {
    const res = await adminApi.getAllUsers(page.value, 10)
    users.value = res.data
    totalPages.value = res.totalPages
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
    alert(e instanceof Error ? e.message : 'Lỗi khi cập nhật trạng thái')
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa người dùng này?')) return
  try {
    await adminApi.deleteUser(id)
    await fetchUsers()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa người dùng')
  }
}

onMounted(fetchUsers)
</script>

<template>
  <AppShell>
    <div class="space-y-6">
      <div>
        <h1 class="text-3xl font-bold">Quản lý người dùng</h1>
        <p class="mt-1 text-zinc-400">Xem và quản lý thông tin các tài khoản trên hệ thống</p>
      </div>

      <div v-if="err" class="rounded-xl bg-red-950/50 p-4 text-red-300 ring-1 ring-red-500/30">
        {{ err }}
      </div>

      <div class="overflow-hidden rounded-2xl border border-white/10 bg-zinc-900/50 backdrop-blur-sm shadow-xl">
        <table class="w-full text-left">
          <thead class="bg-white/5 border-b border-white/10">
            <tr>
              <th class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase">Tài khoản</th>
              <th class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase">Email</th>
              <th class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase">Quyền</th>
              <th class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase">Trạng thái</th>
              <th class="px-6 py-4 text-sm font-semibold text-zinc-300 uppercase tracking-wider uppercase">Hành động</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-white/5">
            <tr v-if="loading" v-for="i in 5" :key="i" class="animate-pulse">
               <td class="px-6 py-4"><div class="h-10 w-24 rounded bg-white/10"></div></td>
               <td class="px-6 py-4"><div class="h-4 w-32 rounded bg-white/10"></div></td>
               <td class="px-6 py-4"><div class="h-4 w-20 rounded bg-white/10"></div></td>
               <td class="px-6 py-4"><div class="h-4 w-16 rounded bg-white/10"></div></td>
               <td class="px-6 py-4"><div class="h-8 w-16 rounded bg-white/10"></div></td>
            </tr>
            <tr v-else v-for="u in users" :key="u.id" class="hover:bg-white/5 transition-colors">
              <td class="px-6 py-4 font-semibold text-zinc-100">{{ u.username }}</td>
              <td class="px-6 py-4 text-sm text-zinc-400">{{ u.email || '-' }}</td>
              <td class="px-6 py-4">
                <div class="flex flex-wrap gap-1">
                  <span v-for="r in u.roles" :key="r.name" class="px-2 py-0.5 text-[10px] uppercase font-black bg-white/5 text-zinc-300 rounded ring-1 ring-white/10">
                    {{ r.name }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4">
                <span :class="[
                  'px-2 py-1 text-xs font-bold rounded-full',
                  u.enabled ? 'bg-emerald-500/10 text-emerald-400 ring-1 ring-emerald-500/30' : 'bg-red-500/10 text-red-400 ring-1 ring-red-500/30'
                ]">
                  {{ u.enabled ? 'Đang hoạt động' : 'Bị khóa' }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm">
                <div class="flex items-center gap-3">
                  <button @click="handleStatusToggle(u.id, u.enabled ?? true)" class="text-violet-400 hover:text-violet-300 transition-colors">
                    {{ u.enabled ? 'Khóa' : 'Mở' }}
                  </button>
                  <button @click="handleDelete(u.id)" class="text-red-400 hover:text-red-300 transition-colors">Xóa</button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && users.length === 0">
              <td colspan="5" class="px-6 py-12 text-center text-zinc-500">Không tìm thấy người dùng nào.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Simple Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-2">
        <button 
          v-for="p in totalPages" 
          :key="p"
          @click="page = p; fetchUsers()"
          :class="[
            'px-4 py-2 rounded-lg font-medium transition-all',
            page === p ? 'bg-violet-600 text-white' : 'bg-white/5 text-zinc-400 hover:bg-white/10'
          ]"
        >
          {{ p }}
        </button>
      </div>
    </div>
  </AppShell>
</template>
