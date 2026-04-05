<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import AppShell from '../components/AppShell.vue'
import { ApiError } from '../api/client'

const username = ref('')
const password = ref('')
const err = ref('')
const loading = ref(false)
const { login } = useAuth()
const router = useRouter()
const route = useRoute()

async function submit() {
  err.value = ''
  loading.value = true
  try {
    await login(username.value.trim(), password.value)
    const r = route.query.redirect as string | undefined
    await router.push(r && r.startsWith('/') ? r : '/')
  } catch (e) {
    err.value = e instanceof ApiError ? e.message : 'Đăng nhập thất bại'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AppShell>
    <div class="mx-auto max-w-md rounded-2xl bg-zinc-900/80 p-8 ring-1 ring-white/10">
      <h1 class="text-2xl font-bold">Đăng nhập</h1>
      <p class="mt-2 text-sm text-zinc-500">Sử dụng tài khoản backend của bạn.</p>

      <form class="mt-8 space-y-4" @submit.prevent="submit">
        <label class="block text-sm">
          <span class="text-zinc-500">Tên đăng nhập</span>
          <input
            v-model="username"
            required
            autocomplete="username"
            class="mt-1 w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
          />
        </label>
        <label class="block text-sm">
          <span class="text-zinc-500">Mật khẩu</span>
          <input
            v-model="password"
            type="password"
            required
            autocomplete="current-password"
            class="mt-1 w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 outline-none focus:ring-2 focus:ring-violet-500"
          />
        </label>
        <p v-if="err" class="text-sm text-red-400">{{ err }}</p>
        <button
          type="submit"
          :disabled="loading"
          class="w-full rounded-xl bg-violet-600 py-3 font-semibold hover:bg-violet-500 disabled:opacity-50"
        >
          {{ loading ? 'Đang xử lý…' : 'Đăng nhập' }}
        </button>
      </form>

      <p class="mt-6 text-center text-sm text-zinc-500">
        Chưa có tài khoản?
        <RouterLink to="/register" class="text-violet-400 hover:underline">Đăng ký</RouterLink>
      </p>
    </div>
  </AppShell>
</template>
