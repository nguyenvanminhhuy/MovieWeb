<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import * as adminApi from '../../api/adminApi'
import type { ReviewResponse } from '../../api/types'

const reviews = ref<ReviewResponse[]>([])
const loading = ref(true)
const page = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const search = ref('')

async function fetchReviews() {
  loading.value = true
  try {
    const res = await adminApi.getAllReviewsAdmin(page.value, 20)
    reviews.value = res.data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: string) {
  if (!confirm('Xóa đánh giá này?')) return
  try {
    await adminApi.deleteReviewAdmin(id)
    await fetchReviews()
  } catch (e) {
    alert('Lỗi: ' + (e instanceof Error ? e.message : String(e)))
  }
}

function changePage(p: number) {
  page.value = p
  fetchReviews()
}

onMounted(fetchReviews)
</script>

<template>
  <AdminLayout>
    <div class="page">
      <div class="page-header">
        <div>
          <h1 class="page-title">Quản lý đánh giá</h1>
          <p class="page-subtitle">{{ totalElements.toLocaleString() }} lượt đánh giá phim</p>
        </div>
      </div>

      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8" />
          <path d="M21 21l-4.35-4.35" stroke-linecap="round" />
        </svg>
        <input v-model="search" placeholder="Tìm theo nội dung đánh giá..." class="search-input" />
      </div>

      <div class="reviews-grid">
        <div v-if="loading" v-for="i in 6" :key="i" class="review-skeleton"></div>
        <div
          v-else
          v-for="r in reviews.filter(
            (x) =>
              !search ||
              x.content?.toLowerCase().includes(search.toLowerCase()) ||
              (x.user?.username || '').toLowerCase().includes(search.toLowerCase()) ||
              (x.movieTitle || '').toLowerCase().includes(search.toLowerCase()),
          )"
          :key="r.id"
          class="review-card"
        >
          <div class="review-top">
            <div class="review-user">
              <div class="user-avatar">{{ (r.user?.username || '?').charAt(0).toUpperCase() }}</div>
              <div>
                <div class="username">{{ r.user?.username || 'Username' }}</div>
                <div class="review-date">
                  {{ r.createdAt ? new Date(r.createdAt).toLocaleDateString() : '—' }}
                </div>
              </div>
            </div>
            <div class="review-rating">⭐ {{ r.rating }}</div>
          </div>
          <div class="review-movie-link">
            <span>Phim: {{ r.movieTitle || 'N/A' }}</span>
          </div>
          <div class="review-content">{{ r.content }}</div>
          <div class="review-footer">
            <button @click="handleDelete(r.id)" class="delete-btn">Xóa đánh giá</button>
          </div>
        </div>
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button @click="changePage(Math.max(1, page - 1))" :disabled="page === 1" class="page-btn">
          ‹
        </button>
        <button
          v-for="p in totalPages"
          :key="p"
          @click="changePage(p)"
          :class="['page-btn', page === p ? 'active' : '']"
        >
          {{ p }}
        </button>
        <button
          @click="changePage(Math.min(totalPages, page + 1))"
          :disabled="page === totalPages"
          class="page-btn"
        >
          ›
        </button>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.page-header {
  display: flex;
  justify-content: space-between;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: #f4f4f5;
  margin: 0;
}
.page-subtitle {
  font-size: 13px;
  color: #71717a;
  margin: 4px 0 0;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  padding: 9px 14px;
  max-width: 320px;
}
.search-box svg {
  width: 14px;
  height: 14px;
  color: #52525b;
}
.search-input {
  background: none;
  border: none;
  outline: none;
  color: #e4e4e7;
  font-size: 13px;
  width: 100%;
}

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}
.review-card {
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 18px;
  display: flex;
  flex-direction: column;
}
.review-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}
.review-user {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #7c3aed;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: white;
}
.username {
  font-size: 13.5px;
  font-weight: 600;
  color: #f4f4f5;
}
.review-date {
  font-size: 11px;
  color: #52525b;
}
.review-rating {
  font-size: 13px;
  font-weight: 700;
  color: #fbbf24;
  background: rgba(251, 191, 36, 0.1);
  padding: 2px 8px;
  border-radius: 6px;
}

.review-movie-link {
  font-size: 11px;
  color: #7c3aed;
  background: rgba(124, 58, 237, 0.05);
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 600;
  margin-bottom: 12px;
}
.review-content {
  font-size: 13.5px;
  color: #a1a1aa;
  line-height: 1.6;
  min-height: 40px;
}
.review-footer {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
.delete-btn {
  font-size: 12px;
  font-weight: 600;
  color: #f87171;
  background: none;
  border: 1px solid rgba(239, 68, 68, 0.2);
  padding: 4px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
}
.delete-btn:hover {
  background: rgba(239, 68, 68, 0.1);
}

.review-skeleton {
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  height: 160px;
  animation: pulse 1.5s infinite;
}
@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 6px;
}
.page-btn {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  color: #71717a;
  cursor: pointer;
}
.page-btn.active {
  background: #7c3aed;
  color: white;
  border-color: #7c3aed;
}
.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}
</style>
