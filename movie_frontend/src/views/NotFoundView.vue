<script setup lang="ts">
import { RouterLink } from 'vue-router'
import AppShell from '../components/AppShell.vue'
</script>

<template>
  <AppShell>
    <div class="py-20 text-center">
      <p class="text-6xl font-bold text-zinc-700">404</p>
      <p class="mt-4 text-zinc-500">Trang không tồn tại.</p>
      <RouterLink to="/" class="mt-8 inline-block text-violet-400 hover:underline">Về trang chủ</RouterLink>
    </div>
  </AppShell>
</template>
