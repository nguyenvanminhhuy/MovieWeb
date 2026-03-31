<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import * as api from '../api/animeApi'
import AppShell from '../components/AppShell.vue'
import { ApiError } from '../api/client'

const username = ref('')
const password = ref('')
const email = ref('')
const err = ref('')
const loading = ref(false)
const router = useRouter()

const envRoleIds = computed(() => {
  const raw = import.meta.env.VITE_REGISTER_ROLE_IDS as string | undefined
  if (!raw?.trim()) return [] as string[]
  return raw
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
})

async function submit() {
  err.value = ''
  loading.value = true
  try {
    await api.registerUser({
      username: username.value.trim(),
      password: password.value,
      email: email.value.trim(),
      roles: envRoleIds.value,
    })
    await router.push('/login')
  } catch (e) {
    err.value = e instanceof ApiError ? e.message : 'Đăng ký thất bại'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AppShell>
    <div class="mx-auto max-w-md rounded-2xl bg-zinc-900/80 p-8 ring-1 ring-white/10">
      <h1 class="text-2xl font-bold">Đăng ký</h1>

      <form class="mt-8 space-y-4" @submit.prevent="submit">
        <label class="block text-sm">
          <span class="text-zinc-500">Tên đăng nhập</span>
          <input
            v-model="username"
            required
            minlength="3"
            autocomplete="username"
            class="mt-1 w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
          />
        </label>
        <label class="block text-sm">
          <span class="text-zinc-500">Email</span>
          <input
            v-model="email"
            type="email"
            required
            autocomplete="email"
            class="mt-1 w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
          />
        </label>
        <label class="block text-sm">
          <span class="text-zinc-500">Mật khẩu (tối thiểu 8 ký tự)</span>
          <input
            v-model="password"
            type="password"
            required
            minlength="8"
            autocomplete="new-password"
            class="mt-1 w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
          />
        </label>
        <p v-if="err" class="text-sm text-red-400">{{ err }}</p>
        <button
          type="submit"
          :disabled="loading"
          class="w-full rounded-xl bg-violet-600 py-3 font-semibold hover:bg-violet-500 disabled:opacity-50"
        >
          {{ loading ? 'Đang xử lý…' : 'Tạo tài khoản' }}
        </button>
      </form>

      <p class="mt-6 text-center text-sm text-zinc-500">
        Đã có tài khoản?
        <RouterLink to="/login" class="text-violet-400 hover:underline">Đăng nhập</RouterLink>
      </p>
    </div>
  </AppShell>
</template>
