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

function getRandomColor(str: string) {
  const colors = ['#7c3aed', '#db2777', '#2563eb', '#059669', '#d97706', '#dc2626']
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
}

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
        <div class="dot"></div>
        {{ err }}
      </div>

      <!-- Filters & Stats -->
      <div class="top-row">
        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8" />
            <path d="M21 21l-4.35-4.35" stroke-linecap="round" />
          </svg>
          <input
            v-model="search"
            placeholder="Tìm theo nội dung, người dùng, phim..."
            class="search-input"
          />
        </div>
        <div class="stats-mini">
          <div class="stat-item">
            <span class="dot"></span>
            <b>{{ totalElements }}</b> bình luận
          </div>
        </div>
      </div>

      <!-- Comment grid -->
      <div class="comments-grid">
        <!-- Loading skeletons -->
        <div v-if="loading" v-for="i in 6" :key="i" class="comment-card skeleton-card">
          <div class="card-header">
            <div class="skeleton avatar"></div>
            <div class="skeleton-meta">
              <div class="skeleton title"></div>
              <div class="skeleton subtitle"></div>
            </div>
          </div>
          <div class="skeleton body"></div>
        </div>

        <!-- Comments -->
        <div
          v-else
          v-for="c in comments.filter(
            (x) =>
              !search ||
              x.content.toLowerCase().includes(search.toLowerCase()) ||
              (x.user?.username || '').toLowerCase().includes(search.toLowerCase()) ||
              (x.movieTitle || '').toLowerCase().includes(search.toLowerCase()),
          )"
          :key="c.id"
          class="comment-card"
        >
          <div class="card-header">
            <div class="user-cell">
              <div class="user-avatar" :style="!c.user?.avatar ? `background: ${getRandomColor(c.user?.username || 'user')}` : ''">
                <img v-if="c.user?.avatar" :src="c.user.avatar" alt="" />
                <span v-else>{{ (c.user?.username || '?').charAt(0).toUpperCase() }}</span>
              </div>
              <div class="user-info">
                <div class="username">{{ c.user?.username || 'Ẩn danh' }}</div>
                <div class="time-stamp">{{ timeAgo(c.createdAt) }}</div>
              </div>
            </div>
            <button @click="handleDelete(c.id)" class="action-btn-delete" title="Xóa bình luận">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6" /><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2" />
              </svg>
            </button>
          </div>
          
          <div class="comment-body">
            <p class="content-text">{{ c.content }}</p>
          </div>

          <div class="card-footer">
            <div class="movie-ref">
              <div class="ref-icon">🎬</div>
              <span class="movie-name">{{ c.movieTitle || 'N/A' }}</span>
            </div>
            <div v-if="c.likes" class="likes-badge">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
              {{ c.likes }}
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="!loading && comments.length === 0" class="empty-state-large">
          <div class="empty-icon">💬</div>
          <h3>Chưa có bình luận</h3>
          <p>Dữ liệu này có thể đã bị xóa hoặc chưa có người dùng nào thảo luận.</p>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-modern">
        <button @click="changePage(Math.max(1, page - 1))" :disabled="page === 1" class="p-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 18l-6-6 6-6"/></svg>
        </button>
        <div class="p-pages">
          <button v-for="p in totalPages" :key="p" @click="changePage(p)" :class="['p-page', page === p ? 'active' : '']">
            {{ p }}
          </button>
        </div>
        <button @click="changePage(Math.min(totalPages, page + 1))" :disabled="page === totalPages" class="p-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 18l6-6-6-6"/></svg>
        </button>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 28px; }
.page-title { font-size: 32px; font-weight: 900; color: #fff; margin: 0; letter-spacing: -0.04em; }
.page-subtitle { font-size: 15px; color: #71717a; margin-top: 6px; }

/* Alert */
.alert { display: flex; align-items: center; gap: 12px; padding: 16px 24px; border-radius: 20px; font-size: 14px; font-weight: 600; margin-bottom: 8px; }
.alert-error { background: rgba(239, 68, 68, 0.1); border: 1px solid rgba(239, 68, 68, 0.2); color: #f87171; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: #ef4444; box-shadow: 0 0 12px #ef4444; }

/* Top Row */
.top-row { display: flex; align-items: center; justify-content: space-between; gap: 20px; flex-wrap: wrap; }
.search-box { display: flex; align-items: center; gap: 12px; background: #18181b; border: 1px solid rgba(255,255,255,0.08); border-radius: 18px; padding: 12px 22px; width: 100%; max-width: 440px; transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1); }
.search-box:focus-within { border-color: #7c3aed; background: #1d1d21; box-shadow: 0 0 0 4px rgba(124, 58, 237, 0.15); }
.search-box svg { width: 18px; height: 18px; color: #52525b; }
.search-input { background: none; border: none; outline: none; color: #fff; font-size: 14px; width: 100%; font-weight: 500; }

.stats-mini { background: #18181b; padding: 10px 20px; border-radius: 16px; border: 1px solid rgba(255,255,255,0.06); }
.stat-item { display: flex; align-items: center; gap: 10px; font-size: 14px; color: #71717a; }
.stat-item .dot { width: 6px; height: 6px; background: #7c3aed; box-shadow: 0 0 8px #7c3aed; }
.stat-item b { color: #fff; font-weight: 800; }

/* Grid */
.comments-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(380px, 1fr)); gap: 20px; }

/* Comment Card */
.comment-card { background: #18181b; border: 1px solid rgba(255,255,255,0.06); border-radius: 24px; padding: 24px; display: flex; flex-direction: column; gap: 18px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); position: relative; overflow: hidden; }
.comment-card:hover { transform: translateY(-4px); border-color: rgba(124, 58, 237, 0.3); background: #1c1c1f; box-shadow: 0 20px 40px rgba(0,0,0,0.4); }

.card-header { display: flex; justify-content: space-between; align-items: flex-start; }
.user-cell { display: flex; align-items: center; gap: 14px; }
.user-avatar { width: 48px; height: 48px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-weight: 900; color: #fff; font-size: 18px; overflow: hidden; flex-shrink: 0; box-shadow: 0 4px 12px rgba(0,0,0,0.2); }
.user-avatar img { width: 100%; height: 100%; object-fit: cover; }
.user-info { display: flex; flex-direction: column; gap: 2px; }
.username { font-weight: 800; color: #fff; font-size: 15px; }
.time-stamp { font-size: 12px; color: #52525b; font-weight: 600; }

.action-btn-delete { width: 40px; height: 40px; border-radius: 12px; border: none; background: rgba(239, 68, 68, 0.05); color: #ef4444; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s; opacity: 0; }
.comment-card:hover .action-btn-delete { opacity: 1; }
.action-btn-delete:hover { background: #ef4444; color: #fff; transform: rotate(8deg); }
.action-btn-delete svg { width: 18px; height: 18px; }

.comment-body { position: relative; }
.content-text { font-size: 15px; color: #a1a1aa; line-height: 1.7; margin: 0; display: -webkit-box; -webkit-line-clamp: 4; -webkit-box-orient: vertical; overflow: hidden; font-weight: 500; }

.card-footer { display: flex; align-items: center; justify-content: space-between; padding-top: 18px; border-top: 1px solid rgba(255,255,255,0.04); }
.movie-ref { display: flex; align-items: center; gap: 8px; background: rgba(255,255,255,0.03); padding: 8px 14px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.05); max-width: 70%; }
.ref-icon { font-size: 14px; }
.movie-name { font-size: 12.5px; font-weight: 700; color: #7c3aed; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.likes-badge { display: flex; align-items: center; gap: 6px; font-size: 13px; font-weight: 800; color: #db2777; background: rgba(219, 39, 119, 0.1); padding: 6px 12px; border-radius: 10px; }
.likes-badge svg { width: 14px; height: 14px; }

/* Pagination */
.pagination-modern { display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 40px; }
.p-pages { display: flex; gap: 8px; }
.p-page { width: 44px; height: 44px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 15px; font-weight: 800; border: 1px solid rgba(255,255,255,0.08); background: #18181b; color: #71717a; cursor: pointer; transition: all 0.2s; }
.p-page.active { background: #7c3aed; color: #fff; border-color: #7c3aed; box-shadow: 0 8px 16px rgba(124, 58, 237, 0.3); }
.p-btn { width: 44px; height: 44px; border-radius: 14px; border: 1px solid rgba(255,255,255,0.08); background: #18181b; color: #71717a; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; }
.p-btn:hover:not(:disabled) { border-color: rgba(255,255,255,0.2); color: #fff; }
.p-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.p-btn svg { width: 22px; height: 22px; }

/* Empty State */
.empty-state-large { grid-column: 1/-1; padding: 100px 20px; background: rgba(255,255,255,0.02); border: 2px dashed rgba(255,255,255,0.05); border-radius: 32px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 16px; }
.empty-icon { font-size: 56px; margin-bottom: 8px; }
.empty-state-large h3 { font-size: 20px; font-weight: 900; color: #fff; margin: 0; }
.empty-state-large p { font-size: 15px; color: #71717a; max-width: 320px; margin: 0; line-height: 1.6; }

/* Skeleton */
.skeleton-card { background: #18181b; transition: none; cursor: default; }
.skeleton { background: linear-gradient(90deg, #18181b 25%, #27272a 50%, #18181b 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; border-radius: 8px; }
@keyframes shimmer { from { background-position: 200% 0; } to { background-position: -200% 0; } }
.skeleton.avatar { width: 48px; height: 48px; border-radius: 16px; }
.skeleton-meta { flex: 1; display: flex; flex-direction: column; gap: 6px; }
.skeleton.title { width: 40%; height: 14px; }
.skeleton.subtitle { width: 25%; height: 10px; }
.skeleton.body { width: 100%; height: 60px; margin-top: 10px; }
</style>
