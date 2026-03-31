<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AppShell from '../../components/AppShell.vue'
import * as adminApi from '../../api/adminApi'
import type { CommentResponse } from '../../api/types'

const comments = ref<CommentResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)

async function fetchComments() {
  loading.value = true
  try {
    const res = await adminApi.getAllCommentsAdmin(page.value, 20)
    comments.value = res.data
    totalPages.value = res.totalPages
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải danh sách bình luận'
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Bạn có chắc chắn muốn xóa bình luận này?')) return
  try {
    await adminApi.deleteCommentAdmin(id)
    await fetchComments()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa bình luận')
  }
}

onMounted(fetchComments)
</script>

<template>
  <AppShell>
    <div class="space-y-6">
      <div>
        <h1 class="text-3xl font-bold">Duyệt bình luận</h1>
        <p class="mt-1 text-zinc-400">Xem và gỡ bỏ các bình luận không phù hợp</p>
      </div>

      <div v-if="err" class="rounded-xl bg-red-950/50 p-4 text-red-300 ring-1 ring-red-500/30">
        {{ err }}
      </div>

      <div class="space-y-4">
        <div v-if="loading" v-for="i in 5" :key="i" class="animate-pulse rounded-2xl bg-white/5 p-6 border border-white/10">
          <div class="flex items-center gap-3">
             <div class="h-10 w-10 rounded-full bg-white/10"></div>
             <div class="space-y-2">
                <div class="h-4 w-24 rounded bg-white/10"></div>
                <div class="h-3 w-32 rounded bg-white/10"></div>
             </div>
          </div>
          <div class="mt-4 h-12 w-full rounded bg-white/10"></div>
        </div>
        <div v-else v-for="c in comments" :key="c.id" class="group relative rounded-2xl border border-white/10 bg-zinc-900/50 p-6 backdrop-blur-sm transition-all hover:bg-white/5">
          <div class="flex items-start justify-between">
            <div class="flex items-center gap-4">
              <div class="h-12 w-12 rounded-full bg-violet-600/20 flex items-center justify-center text-violet-400 font-bold overflow-hidden ring-1 ring-white/10 shadow-lg">
                <img v-if="c.user?.avatar" :src="c.user.avatar" class="h-full w-full object-cover" />
                <span v-else>{{ c.user?.username.charAt(0).toUpperCase() }}</span>
              </div>
              <div>
                <div class="font-bold text-zinc-100 flex items-center gap-2">
                   {{ c.user?.username }}
                   <span class="text-[10px] text-zinc-500 font-normal">{{ c.id }}</span>
                </div>
                <div class="text-xs text-zinc-500">{{ c.createdAt ? new Date(c.createdAt).toLocaleString('vi-VN') : '-' }}</div>
              </div>
            </div>
            <button @click="handleDelete(c.id)" class="rounded-lg bg-red-500/10 p-2 text-red-400 opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-500/20">
               <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                 <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
               </svg>
            </button>
          </div>
          <p class="mt-4 text-zinc-300 leading-relaxed">{{ c.content }}</p>
        </div>
        <div v-if="!loading && comments.length === 0" class="py-12 text-center text-zinc-500">Không có bình luận nào để hiển thị.</div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-2 py-6">
        <button 
          v-for="p in totalPages" 
          :key="p"
          @click="page = p; fetchComments()"
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
