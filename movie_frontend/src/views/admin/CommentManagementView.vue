<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import type { CommentResponse } from '../../api/types'

const comments = ref<CommentResponse[]>([])
const loading = ref(true)
const err = ref('')
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')

async function fetchComments() {
  loading.value = true
  err.value = ''
  try {
    const res = await adminApi.getAllCommentsAdmin(page.value, 20)
    comments.value = res.data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không thể tải bình luận'
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Xóa bình luận này?')) return
  try {
    await adminApi.deleteCommentAdmin(id)
    await fetchComments()
  } catch (e) {
    alert(e instanceof Error ? e.message : 'Lỗi khi xóa')
  }
}

function changePage(p: number) {
  page.value = p
  fetchComments()
}

onMounted(fetchComments)

function timeAgo(dateStr?: string) {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  const diff = Date.now() - d.getTime()
  const mins = Math.floor(diff / 60000)
  if (mins < 1) return 'Vừa xong'
  if (mins < 60) return `${mins} phút trước`
  const hrs = Math.floor(mins / 60)
  if (hrs < 24) return `${hrs} giờ trước`
  const days = Math.floor(hrs / 24)
  return `${days} ngày trước`
}
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Kiểm duyệt bình luận</h1>
          <p class="page-subtitle">{{ totalElements.toLocaleString() }} bình luận trong hệ thống</p>
        </div>
      </div>

      <div v-if="err" class="alert alert-error">
        <div class="dot"></div>{{ err }}
      </div>

      <!-- Search -->
      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35" stroke-linecap="round"/>
        </svg>
        <input v-model="search" placeholder="Tìm theo nội dung, tên người dùng..." class="search-input" />
      </div>

      <!-- Comment cards -->
      <div class="comments-grid">
        <!-- Loading skeletons -->
        <div v-if="loading" v-for="i in 6" :key="i" class="comment-skeleton">
          <div class="flex gap-3 items-center mb-3">
            <div class="skeleton" style="width:40px;height:40px;border-radius:50%"></div>
            <div>
              <div class="skeleton" style="width:100px;height:13px;margin-bottom:5px"></div>
              <div class="skeleton" style="width:70px;height:11px"></div>
            </div>
          </div>
          <div class="skeleton" style="width:100%;height:40px;border-radius:8px"></div>
        </div>

        <!-- Comments -->
        <div
          v-else
          v-for="c in comments.filter(x => !search || x.content.toLowerCase().includes(search.toLowerCase()) || (x.user?.username || '').toLowerCase().includes(search.toLowerCase()))"
          :key="c.id"
          class="comment-card"
        >
          <div class="comment-top">
            <div class="comment-user">
              <div class="user-avatar">
                <img v-if="c.user?.avatar" :src="c.user.avatar" alt="" />
                <span v-else>{{ (c.user?.username || '?').charAt(0).toUpperCase() }}</span>
              </div>
              <div>
                <div class="username">{{ c.user?.username || 'Ẩn danh' }}</div>
                <div class="comment-meta">{{ timeAgo(c.createdAt) }}</div>
              </div>
            </div>
            <button @click="handleDelete(c.id)" class="delete-btn" title="Xóa bình luận">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6M10 11v6M14 11v6M9 6V4h6v2"/>
              </svg>
            </button>
          </div>
          <p class="comment-content">{{ c.content }}</p>
          <div v-if="c.likes" class="comment-footer">
            <span class="like-count">❤ {{ c.likes }} lượt thích</span>
          </div>
        </div>

        <div v-if="!loading && comments.length === 0" class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
          </svg>
          <p>Không có bình luận nào.</p>
        </div>
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
.page { display: flex; flex-direction: column; gap: 20px; }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; }
.page-title { font-size: 24px; font-weight: 800; color: #f4f4f5; margin: 0; letter-spacing: -0.02em; }
.page-subtitle { font-size: 13px; color: #71717a; margin: 4px 0 0; }

.alert { display: flex; align-items: center; gap: 10px; padding: 11px 16px; border-radius: 12px; font-size: 13px; }
.alert-error { background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.2); color: #f87171; }
.dot { width: 7px; height: 7px; border-radius: 50%; background: #ef4444; flex-shrink: 0; animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }

.search-box { display: flex; align-items: center; gap: 8px; background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; padding: 9px 14px; max-width: 380px; }
.search-box svg { width: 15px; height: 15px; color: #52525b; flex-shrink: 0; }
.search-input { background: none; border: none; outline: none; color: #e4e4e7; font-size: 13px; width: 100%; }
.search-input::placeholder { color: #52525b; }

.comments-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 14px; }

.comment-skeleton { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; padding: 18px; animation: pulse 1.5s infinite; }
.flex { display: flex; } .gap-3 { gap: 12px; } .items-center { align-items: center; } .mb-3 { margin-bottom: 12px; }
.skeleton { background: rgba(255,255,255,0.07); border-radius: 4px; }

.comment-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 14px; padding: 18px; transition: all 0.15s; }
.comment-card:hover { background: #1c1c1f; border-color: rgba(255,255,255,0.1); }

.comment-top { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 12px; }
.comment-user { display: flex; align-items: center; gap: 10px; }
.user-avatar { width: 38px; height: 38px; border-radius: 50%; background: linear-gradient(135deg,#7c3aed,#a855f7); display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; color: white; flex-shrink: 0; overflow: hidden; }
.user-avatar img { width:100%;height:100%;object-fit:cover; }
.username { font-size: 13.5px; font-weight: 600; color: #e4e4e7; }
.comment-meta { font-size: 11px; color: #52525b; margin-top: 2px; }

.delete-btn { width: 32px; height: 32px; border-radius: 8px; border: none; background: rgba(239,68,68,0.08); color: #f87171; cursor: pointer; display: flex; align-items: center; justify-content: center; opacity: 0; transition: all 0.15s; }
.comment-card:hover .delete-btn { opacity: 1; }
.delete-btn:hover { background: rgba(239,68,68,0.18); }
.delete-btn svg { width: 15px; height: 15px; }

.comment-content { font-size: 13.5px; color: #a1a1aa; line-clamp: 4; -webkit-line-clamp: 4; line-height: 1.6; margin: 0; display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; }
.comment-footer { display: flex; align-items: center; margin-top: 10px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.05); }
.like-count { font-size: 11px; color: #71717a; }

.empty-state { grid-column: 1 / -1; display: flex; flex-direction: column; align-items: center; gap: 12px; color: #52525b; padding: 48px; }
.empty-state svg { width: 40px; height: 40px; }
.empty-state p { font-size: 14px; }

.pagination { display: flex; justify-content: center; gap: 6px; }
.page-btn { padding: 7px 13px; border-radius: 9px; font-size: 13px; font-weight: 500; border: 1px solid rgba(255,255,255,0.06); background: #18181b; color: #71717a; cursor: pointer; transition: all 0.15s; }
.page-btn:hover:not(:disabled) { background: #27272a; color: #e4e4e7; }
.page-btn.active { background: #7c3aed; color: white; border-color: #7c3aed; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
</style>
