<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as api from '../api/animeApi'
import type { UserResponse } from '../api/types'
import AppShell from '../components/AppShell.vue'
import { mediaUrl } from '../utils/media'

const user = ref<UserResponse | null>(null)
const loading = ref(true)
const saving = ref(false)
const err = ref('')
const msg = ref('')

const editMode = ref(false)
const form = ref({
  email: '',
  fullName: '',
  avatar: '',
})

async function fetchUser() {
  loading.value = true
  err.value = ''
  try {
    const res = await api.getMyInfo()
    user.value = res
    form.value = {
      email: res.email || '',
      fullName: res.fullName || '',
      avatar: res.avatar || '',
    }
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không tải được'
  } finally {
    loading.value = false
  }
}

onMounted(fetchUser)

async function handleSave() {
  saving.value = true
  msg.value = ''
  try {
    await api.updateProfile(form.value)
    msg.value = 'Cập nhật thành công!'
    editMode.value = false
    await fetchUser()
  } catch (e) {
    msg.value = e instanceof Error ? e.message : 'Lỗi khi lưu'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <AppShell>
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-2xl font-bold text-white">Tài khoản</h1>
      <button
        v-if="user && !editMode"
        @click="editMode = true"
        class="rounded-lg bg-zinc-800 px-4 py-2 text-sm font-medium hover:bg-zinc-700"
      >
        Chỉnh sửa
      </button>
    </div>

    <div v-if="loading" class="py-20 text-center text-zinc-500">Đang tải…</div>
    <div v-else-if="err" class="rounded-xl bg-red-950/50 p-4 text-red-300 ring-1 ring-red-500/30">
      {{ err }}
    </div>

    <div v-else-if="user" class="max-w-2xl space-y-6">
      <div
        class="flex flex-col gap-8 rounded-2xl bg-zinc-900/60 p-8 ring-1 ring-white/10 sm:flex-row"
      >
        <div class="flex shrink-0 justify-center">
          <div
            class="group relative h-32 w-32 overflow-hidden rounded-full bg-zinc-800 ring-4 ring-violet-500/20"
          >
            <img
              v-if="user.avatar && mediaUrl(user.avatar)"
              :src="mediaUrl(user.avatar)"
              alt=""
              class="h-full w-full object-cover"
            />
            <div v-else class="flex h-full w-full items-center justify-center text-3xl text-zinc-600">
              {{ (user.username || '?').slice(0, 1).toUpperCase() }}
            </div>
          </div>
        </div>

        <div v-if="!editMode" class="min-w-0 flex-1 space-y-4">
          <div class="grid gap-4 sm:grid-cols-2">
            <div>
              <p class="text-xs font-medium uppercase tracking-wider text-zinc-500">Tên đăng nhập</p>
              <p class="mt-1 font-semibold text-zinc-200">{{ user.username }}</p>
            </div>
            <div>
              <p class="text-xs font-medium uppercase tracking-wider text-zinc-500">Email</p>
              <p class="mt-1 text-zinc-300">{{ user.email || '—' }}</p>
            </div>
            <div>
              <p class="text-xs font-medium uppercase tracking-wider text-zinc-500">Họ tên</p>
              <p class="mt-1 text-zinc-300">{{ user.fullName || '—' }}</p>
            </div>
            <div>
              <p class="text-xs font-medium uppercase tracking-wider text-zinc-500">Ngày tham gia</p>
              <p class="mt-1 text-zinc-300">
                {{ user.createdAt ? new Date(user.createdAt).toLocaleDateString('vi-VN') : '—' }}
              </p>
            </div>
          </div>

          <div v-if="user.roles?.length" class="pt-2">
            <p class="text-xs font-medium uppercase tracking-wider text-zinc-500">Vai trò</p>
            <div class="mt-2 flex flex-wrap gap-2">
              <span
                v-for="r in user.roles"
                :key="r.id"
                class="rounded-md bg-violet-500/10 px-2 py-1 text-xs font-bold text-violet-400 ring-1 ring-violet-500/20"
              >
                {{ r.name }}
              </span>
            </div>
          </div>
        </div>

        <form v-else @submit.prevent="handleSave" class="flex-1 space-y-4">
          <div class="grid gap-4 sm:grid-cols-2">
            <label class="block space-y-1">
              <span class="text-xs font-medium text-zinc-500">Họ tên</span>
              <input
                v-model="form.fullName"
                class="w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-violet-500"
                placeholder="Nguyễn Văn A"
              />
            </label>
            <label class="block space-y-1">
              <span class="text-xs font-medium text-zinc-500">Email</span>
              <input
                v-model="form.email"
                type="email"
                class="w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-violet-500"
              />
            </label>
            <label class="block space-y-1 sm:col-span-2">
              <span class="text-xs font-medium text-zinc-500">Link ảnh đại diện (URL)</span>
              <input
                v-model="form.avatar"
                class="w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-violet-500"
                placeholder="https://..."
              />
            </label>
          </div>

          <div class="flex items-center gap-3 pt-4">
            <button
              type="submit"
              :disabled="saving"
              class="rounded-lg bg-violet-600 px-6 py-2 text-sm font-semibold text-white hover:bg-violet-500 disabled:opacity-50"
            >
              {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
            <button
              type="button"
              @click="editMode = false"
              class="rounded-lg border border-white/10 px-6 py-2 text-sm font-medium hover:bg-white/5"
            >
              Hủy
            </button>
          </div>
        </form>
      </div>

      <p v-if="msg" :class="['text-sm', msg.includes('thành công') ? 'text-green-400' : 'text-red-400']">
        {{ msg }}
      </p>
    </div>
  </AppShell>
</template>
