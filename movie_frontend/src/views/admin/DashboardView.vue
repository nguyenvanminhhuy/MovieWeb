<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '../../components/AdminLayout.vue'
import { RouterLink } from 'vue-router'
import * as adminApi from '../../api/adminApi'
import * as animeSeeder from '../../utils/animeSeeder'

const seeding = ref<string | null>(null)

async function handleSeed(type: string) {
  seeding.value = type
  try {
    let count = 0
    if (type === 'anilist') count = await animeSeeder.seedFromAniList(20)
    else if (type === 'meta') {
      await animeSeeder.seedMetadataOnly(40)
      alert('Đã nạp xong danh sách Thể loại và Studio phổ biến!')
      await loadStats()
      return
    }
    else if (type === 'jikan') count = await animeSeeder.seedTopAnime(10)
    else if (type === 'kitsu') count = await animeSeeder.seedFromKitsu(10)
    alert(`Nạp thành công ${count} bộ phim kèm đầy đủ metadata!`)
    await loadStats()
  } catch (e) {
    alert('Lỗi khi nạp: ' + e)
  } finally {
    seeding.value = null
  }
}

const stats = ref({
  movies: 0,
  users: 0,
  comments: 0,
  genres: 0,
})
const loading = ref(true)

async function loadStats() {
  loading.value = true
  try {
    // Attempt to get real stats from the new endpoint
    const res = await adminApi.getAdminStats()
    stats.value = {
      movies: res.movies || 0,
      users: res.users || 0,
      comments: res.comments || 0,
      genres: res.genres || 0,
    }
  } catch (e) {
    // Fallback to legacy behavior if endpoint not ready
    const [movies, users, comments, genres] = await Promise.allSettled([
      adminApi.getAllMoviesAdmin(1, 1),
      adminApi.getAllUsers(1, 1),
      adminApi.getAllCommentsAdmin(1, 1),
      adminApi.getAllGenresAdmin(),
    ])
    if (movies.status === 'fulfilled') stats.value.movies = movies.value.totalElements
    if (users.status === 'fulfilled') stats.value.users = users.value.totalElements
    if (comments.status === 'fulfilled') stats.value.comments = comments.value.totalElements
    if (genres.status === 'fulfilled') stats.value.genres = genres.value.length
  } finally {
    loading.value = false
  }
}

onMounted(loadStats)

const cards = [
  {
    key: 'movies',
    label: 'Tổng phim',
    subtitle: 'Anime & Movies',
    to: { name: 'admin-movies' },
    color: 'violet',
    icon: 'movie',
  },
  {
    key: 'users',
    label: 'Người dùng',
    subtitle: 'Tài khoản đăng ký',
    to: { name: 'admin-users' },
    color: 'blue',
    icon: 'users',
  },
  {
    key: 'comments',
    label: 'Bình luận',
    subtitle: 'Chờ kiểm duyệt',
    to: { name: 'admin-comments' },
    color: 'amber',
    icon: 'comments',
  },
  {
    key: 'genres',
    label: 'Thể loại',
    subtitle: 'Genres & Tags',
    to: { name: 'admin-genres' },
    color: 'emerald',
    icon: 'genre',
  },
]

const quickActions = [
  {
    label: 'Quản lý phim',
    desc: 'Thêm, sửa, xóa nội dung phim',
    to: { name: 'admin-movies' },
    color: 'violet',
    icon: 'movie',
  },
  {
    label: 'Thể loại',
    desc: 'Quản lý genres và tags',
    to: { name: 'admin-genres' },
    color: 'emerald',
    icon: 'genre',
  },
  {
    label: 'Studio',
    desc: 'Quản lý nhà sản xuất',
    to: { name: 'admin-studios' },
    color: 'sky',
    icon: 'studio',
  },
  {
    label: 'Franchise',
    desc: 'Quản lý series/franchise',
    to: { name: 'admin-franchises' },
    color: 'orange',
    icon: 'franchise',
  },
  {
    label: 'Người dùng',
    desc: 'Kiểm soát tài khoản',
    to: { name: 'admin-users' },
    color: 'blue',
    icon: 'users',
  },
  {
    label: 'Bình luận',
    desc: 'Kiểm duyệt nội dung',
    to: { name: 'admin-comments' },
    color: 'amber',
    icon: 'comments',
  },
  {
    label: 'Đánh giá',
    desc: 'Quản lý rating và review',
    to: { name: 'admin-reviews' },
    color: 'rose',
    icon: 'reviews',
  },
  {
    label: 'Nhật ký hệ thống',
    desc: 'Theo dõi audit logs',
    to: { name: 'admin-audit-logs' },
    color: 'zinc',
    icon: 'audit',
  },
]
</script>

<template>
  <AdminLayout>
    <div class="dashboard">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Dashboard</h1>
          <p class="page-subtitle">Tổng quan hệ thống AnimeStream</p>
        </div>
        <div class="header-badge">
          <span class="online-dot"></span>
          <span>Hệ thống hoạt động</span>
        </div>
      </div>

      <!-- Stats Grid -->
      <div class="stats-grid">
        <RouterLink
          v-for="card in cards"
          :key="card.key"
          :to="card.to"
          :class="['stat-card', `stat-card--${card.color}`]"
        >
          <div class="stat-icon-wrap">
            <!-- Movie icon -->
            <svg
              v-if="card.icon === 'movie'"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <rect x="2" y="2" width="20" height="20" rx="2.18" />
              <line x1="7" y1="2" x2="7" y2="22" />
              <line x1="17" y1="2" x2="17" y2="22" />
              <line x1="2" y1="12" x2="22" y2="12" />
            </svg>
            <svg
              v-else-if="card.icon === 'users'"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75" />
            </svg>
            <svg
              v-else-if="card.icon === 'comments'"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" />
            </svg>
            <svg
              v-else-if="card.icon === 'genre'"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <path d="M4 6h16M4 12h16M4 18h7" stroke-linecap="round" />
            </svg>
          </div>
          <div class="stat-body">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value">
              <span v-if="loading" class="stat-skeleton"></span>
              <span v-else>{{ stats[card.key as keyof typeof stats].toLocaleString() }}</span>
            </div>
            <div class="stat-sub">{{ card.subtitle }}</div>
          </div>
          <div class="stat-arrow">→</div>
        </RouterLink>
      </div>

      <!-- Data Seeding -->
      <div class="section">
        <h2 class="section-title">Nạp dữ liệu từ nguồn ngoài</h2>
        <div class="seeder-grid">
          <div class="seeder-card">
            <div class="seeder-info">
              <h3>AniList Pro Seeder</h3>
              <p>Tự động nạp Phim + Thể loại + Studio + Franchise cùng lúc.</p>
            </div>
            <div class="flex flex-col gap-2">
              <button @click="handleSeed('anilist')" :disabled="seeding !== null" class="seed-btn">
                {{ seeding === 'anilist' ? 'Đang nạp phim...' : 'Nạp Phim + Meta' }}
              </button>
              <button @click="handleSeed('meta')" :disabled="seeding !== null" class="seed-btn outline">
                {{ seeding === 'meta' ? 'Đang đồng bộ...' : 'Chỉ đồng bộ Meta' }}
              </button>
            </div>
          </div>
          <div class="seeder-card legacy">
            <div class="seeder-info">
              <h3>Nguồn dữ liệu khác</h3>
              <p>Jikan, Kitsu, TMDb (Yêu cầu API Key).</p>
            </div>
            <div class="flex gap-2">
              <button @click="handleSeed('jikan')" :disabled="seeding !== null" class="seed-btn sm">Jikan</button>
              <button @click="handleSeed('kitsu')" :disabled="seeding !== null" class="seed-btn sm">Kitsu</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="section">
        <h2 class="section-title">Quản lý nhanh</h2>
        <div class="quick-grid">
          <RouterLink
            v-for="action in quickActions"
            :key="action.label"
            :to="action.to"
            :class="['quick-card', `quick-card--${action.color}`]"
          >
            <div class="quick-icon">
              <svg
                v-if="action.icon === 'movie'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <rect x="2" y="2" width="20" height="20" rx="2.18" />
                <line x1="7" y1="2" x2="7" y2="22" />
                <line x1="17" y1="2" x2="17" y2="22" />
                <line x1="2" y1="12" x2="22" y2="12" />
              </svg>
              <svg
                v-else-if="action.icon === 'genre'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M4 6h16M4 12h16M4 18h7" stroke-linecap="round" />
              </svg>
              <svg
                v-else-if="action.icon === 'studio'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
                <polyline points="9 22 9 12 15 12 15 22" />
              </svg>
              <svg
                v-else-if="action.icon === 'franchise'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <circle cx="12" cy="12" r="10" />
                <path d="M12 8v4l3 3" />
              </svg>
              <svg
                v-else-if="action.icon === 'users'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75" />
              </svg>
              <svg v-else-if="action.icon === 'comments'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" />
              </svg>
              <svg v-else-if="action.icon === 'reviews'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
              </svg>
              <svg v-else-if="action.icon === 'audit'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9" />
              </svg>
            </div>
            <div>
              <div class="quick-label">{{ action.label }}</div>
              <div class="quick-desc">{{ action.desc }}</div>
            </div>
          </RouterLink>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}
.page-title {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: #f4f4f5;
  margin: 0;
}
.page-subtitle {
  font-size: 14px;
  color: #71717a;
  margin: 4px 0 0;
}
.header-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(34, 197, 94, 0.08);
  border: 1px solid rgba(34, 197, 94, 0.2);
  border-radius: 20px;
  padding: 6px 14px;
  font-size: 13px;
  color: #4ade80;
  font-weight: 500;
}
.online-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #4ade80;
  animation: pulse-dot 2s infinite;
}
@keyframes pulse-dot {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

/* Stat cards */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: #18181b;
  text-decoration: none;
  color: inherit;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}
.stat-card::before {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.2s;
}
.stat-card:hover::before {
  opacity: 1;
}
.stat-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.12);
}

.stat-card--violet .stat-icon-wrap {
  background: rgba(124, 58, 237, 0.15);
  color: #a78bfa;
}
.stat-card--violet:hover {
  box-shadow: 0 8px 32px rgba(124, 58, 237, 0.15);
}
.stat-card--blue .stat-icon-wrap {
  background: rgba(59, 130, 246, 0.15);
  color: #60a5fa;
}
.stat-card--blue:hover {
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
}
.stat-card--amber .stat-icon-wrap {
  background: rgba(245, 158, 11, 0.15);
  color: #fbbf24;
}
.stat-card--amber:hover {
  box-shadow: 0 8px 32px rgba(245, 158, 11, 0.15);
}
.stat-card--emerald .stat-icon-wrap {
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}
.stat-card--emerald:hover {
  box-shadow: 0 8px 32px rgba(16, 185, 129, 0.15);
}

.stat-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-icon-wrap svg {
  width: 22px;
  height: 22px;
}
.stat-body {
  flex: 1;
  min-width: 0;
}
.stat-label {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: #71717a;
}
.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: #f4f4f5;
  line-height: 1.2;
  margin: 2px 0;
}
.stat-sub {
  font-size: 11px;
  color: #52525b;
}
.stat-skeleton {
  display: inline-block;
  width: 60px;
  height: 26px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 6px;
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
.stat-arrow {
  font-size: 18px;
  color: #3f3f46;
  transition:
    transform 0.2s,
    color 0.2s;
}
.stat-card:hover .stat-arrow {
  transform: translateX(4px);
  color: #71717a;
}

/* Section */
.section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #e4e4e7;
  margin: 0;
}

/* Quick grid */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}
.quick-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: #18181b;
  text-decoration: none;
  color: inherit;
  transition: all 0.18s;
}
.quick-card:hover {
  background: #1c1c1f;
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.quick-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.quick-icon svg {
  width: 20px;
  height: 20px;
}

.quick-card--violet .quick-icon {
  background: rgba(124, 58, 237, 0.15);
  color: #a78bfa;
}
.quick-card--emerald .quick-icon {
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}
.quick-card--sky .quick-icon {
  background: rgba(14, 165, 233, 0.15);
  color: #38bdf8;
}
.quick-card--orange .quick-icon {
  background: rgba(249, 115, 22, 0.15);
  color: #fb923c;
}
.quick-card--blue .quick-icon {
  background: rgba(59, 130, 246, 0.15);
  color: #60a5fa;
}
.quick-card--amber .quick-icon {
  background: rgba(245, 158, 11, 0.15);
  color: #fbbf24;
}
.quick-card--rose .quick-icon {
  background: rgba(244, 63, 94, 0.15);
  color: #fb7185;
}
.quick-card--zinc .quick-icon {
  background: rgba(113, 113, 122, 0.15);
  color: #a1a1aa;
}

.quick-label {
  font-size: 14px;
  font-weight: 600;
  color: #e4e4e7;
}
.quick-desc {
  font-size: 12px;
  color: #71717a;
  margin-top: 2px;
}

/* Seeder Card Styles */
.seeder-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.seeder-card {
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.1), rgba(167, 139, 250, 0.05));
  border: 1px solid rgba(124, 58, 237, 0.2);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.seeder-card.legacy {
  background: #18181b;
  border-color: rgba(255, 255, 255, 0.06);
}
.seeder-info h3 { margin: 0; font-size: 16px; color: #f4f4f5; }
.seeder-info p { margin: 4px 0 0; font-size: 12px; color: #71717a; }

.seed-btn {
  background: #7c3aed;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 13.5px;
  cursor: pointer;
  transition: all 0.2s;
}
.seed-btn:hover:not(:disabled) { background: #6d28d9; transform: scale(1.02); }
.seed-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.seed-btn.sm { padding: 6px 12px; font-size: 12px; background: #27272a; border: 1px solid rgba(255,255,255,0.1); }
.seed-btn.sm:hover { background: #3f3f46; }

.flex { display: flex; }
.gap-2 { gap: 8px; }
</style>
