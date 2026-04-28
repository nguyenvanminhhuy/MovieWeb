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
      alert('Đã nạp xong danh sách Thể loại, Studio và Franchise phổ biến!')
      await loadStats()
      return
    } else if (type === 'genres') {
      count = await animeSeeder.seedGenres(50)
      alert(`Đã nạp thành công ${count} thể loại!`)
      await loadStats()
      return
    } else if (type === 'studios') {
      count = await animeSeeder.seedStudios(30)
      alert(`Đã nạp thành công dữ liệu Studio từ top Anime!`)
      await loadStats()
      return
    } else if (type === 'franchises') {
      count = await animeSeeder.seedFranchises(30)
      alert(`Đã nạp thành công dữ liệu Franchise!`)
      await loadStats()
      return
    } else if (type === 'jikan') count = await animeSeeder.seedTopAnime(10)
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
  totalUsers: 0,
  totalMovies: 0,
  totalEpisodes: 0,
  totalViews: 0,
  activeUsers: 0,
  newUsersThisMonth: 0,
  totalComments: 0,
  totalReviews: 0,
  totalReports: 0,
  unresolvedReports: 0,
  totalGenres: 0,
  totalStudios: 0,
  totalFranchises: 0,
})
const loading = ref(true)

async function loadStats() {
  loading.value = true
  try {
    const res = await adminApi.getAdminStats()
    stats.value = { ...stats.value, ...res }
  } catch (e) {
    console.error('Lỗi tải stats:', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadStats)

function getStatValue(key: string) {
  const s = stats.value as any
  let val = s[key]

  // Fallback for movies
  if (key === 'totalMovies' && (val === 0 || val === undefined)) {
    val = s.movieCount ?? s.totalMovie ?? s.moviesCount ?? val
  }
  // Fallback for users
  if (key === 'totalUsers' && (val === 0 || val === undefined)) {
    val = s.userCount ?? s.totalUser ?? s.usersCount ?? val
  }
  // Fallback for comments
  if (key === 'totalComments' && (val === 0 || val === undefined)) {
    val = s.commentCount ?? s.totalComment ?? s.commentsCount ?? val
  }
  // Fallback for views
  if (key === 'totalViews' && (val === 0 || val === undefined)) {
    val = s.viewCount ?? s.totalView ?? s.viewsCount ?? val
  }
  // Fallback for episodes
  if (key === 'totalEpisodes' && (val === 0 || val === undefined)) {
    val = s.episodeCount ?? s.totalEpisode ?? s.episodesCount ?? val
  }
  // Fallback for reviews
  if (key === 'totalReviews' && (val === 0 || val === undefined)) {
    val = s.reviewCount ?? s.totalReview ?? s.reviewsCount ?? val
  }
  // Fallback for reports
  if (key === 'totalReports' && (val === 0 || val === undefined)) {
    val = s.reportCount ?? s.totalReport ?? s.reportsCount ?? val
  }
  // Fallback for genres
  if (key === 'totalGenres' && (val === 0 || val === undefined)) {
    val = s.genreCount ?? s.totalGenre ?? s.genresCount ?? val
  }
  // Fallback for studios
  if (key === 'totalStudios' && (val === 0 || val === undefined)) {
    val = s.studioCount ?? s.totalStudio ?? s.studiosCount ?? val
  }
  // Fallback for franchises
  if (key === 'totalFranchises' && (val === 0 || val === undefined)) {
    val = s.franchiseCount ?? s.totalFranchise ?? s.franchisesCount ?? val
  }

  if (typeof val === 'number') return val.toLocaleString()
  return val !== undefined && val !== null ? val.toString() : '0'
}

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

      <!-- Detailed Stats -->
      <div class="section">
        <div class="section-header">
          <h2 class="section-title">Thống kê chi tiết</h2>
          <span class="update-time">Cập nhật vừa xong</span>
        </div>
        <div class="stats-mini-grid">
          <!-- Content Group -->
          <div class="stat-card-mini highlight">
            <div class="mini-icon">🎬</div>
            <div>
              <div class="stat-label">Tổng số phim</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalMovies') }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <div class="mini-icon">🎞</div>
            <div>
              <div class="stat-label">Tổng tập phim</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalEpisodes') }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <div class="mini-icon">👁</div>
            <div>
              <div class="stat-label">Tổng lượt xem</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalViews') }}</div>
            </div>
          </div>

          <!-- User Group -->
          <div class="stat-card-mini">
            <div class="mini-icon">👤</div>
            <div>
              <div class="stat-label">Tổng Users</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalUsers') }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <div class="mini-icon">🔥</div>
            <div>
              <div class="stat-label">Đang hoạt động</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('activeUsers') }}</div>
            </div>
          </div>
          <div class="stat-card-mini highlight">
            <div class="mini-icon">✨</div>
            <div>
              <div class="stat-label">User mới (tháng)</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">+{{ getStatValue('newUsersThisMonth') }}</div>
            </div>
          </div>

          <!-- Metadata Group -->
          <div class="stat-card-mini">
            <div class="mini-icon">🏷</div>
            <div>
              <div class="stat-label">Thể loại</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalGenres') }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <div class="mini-icon">🏢</div>
            <div>
              <div class="stat-label">Studios</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalStudios') }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <div class="mini-icon">🔗</div>
            <div>
              <div class="stat-label">Franchises</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalFranchises') }}</div>
            </div>
          </div>

          <!-- Feedback Group -->
          <div class="stat-card-mini">
            <div class="mini-icon">💬</div>
            <div>
              <div class="stat-label">Bình luận</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalComments') }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <div class="mini-icon">⭐️</div>
            <div>
              <div class="stat-label">Đánh giá</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalReviews') }}</div>
            </div>
          </div>
          <div class="stat-card-mini">
            <div class="mini-icon">⚠️</div>
            <div>
              <div class="stat-label">Báo cáo lỗi</div>
              <div v-if="loading" class="stat-skeleton"></div>
              <div v-else class="stat-value">{{ getStatValue('totalReports') }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="section">
        <h2 class="section-title">Công cụ quản trị</h2>
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
              <svg
                v-else-if="action.icon === 'comments'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" />
              </svg>
              <svg
                v-else-if="action.icon === 'reviews'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path
                  d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"
                />
              </svg>
              <svg
                v-else-if="action.icon === 'audit'"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
                <polyline points="14 2 14 8 20 8" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
                <polyline points="10 9 9 9 8 9" />
              </svg>
            </div>
            <div class="quick-info">
              <div class="quick-label">{{ action.label }}</div>
              <div class="quick-desc">{{ action.desc }}</div>
            </div>
          </RouterLink>
        </div>
      </div>

      <!-- Data Seeding -->
      <div class="section">
        <h2 class="section-title">Hệ thống nạp dữ liệu</h2>
        <div class="seeder-grid">
          <div class="seeder-card pro">
            <div class="seeder-main">
              <div class="seeder-icon pro">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" />
                </svg>
              </div>
              <div class="seeder-info">
                <h3>AniList Pro Engine</h3>
                <p>Giải pháp nạp dữ liệu toàn diện: Phim, Tập, Thể loại, Studio, Franchise.</p>
              </div>
            </div>
            <div class="seeder-actions">
              <button
                @click="handleSeed('anilist')"
                :disabled="seeding !== null"
                class="btn-seed primary"
              >
                {{ seeding === 'anilist' ? 'Đang nạp dữ liệu...' : 'Bắt đầu nạp phim' }}
              </button>
              <button
                @click="handleSeed('meta')"
                :disabled="seeding !== null"
                class="btn-seed secondary"
              >
                Đồng bộ Metadata
              </button>
            </div>
          </div>

          <div class="seeder-card-group">
            <div class="seeder-mini-card">
              <div class="mini-info">
                <h4>Phân loại</h4>
                <p>Thể loại, Studio, Series</p>
              </div>
              <div class="mini-btns">
                <button @click="handleSeed('genres')" :disabled="seeding !== null">
                  Cập nhật Genres
                </button>
                <button @click="handleSeed('studios')" :disabled="seeding !== null">
                  Cập nhật Studios
                </button>
              </div>
            </div>
            <div class="seeder-mini-card">
              <div class="mini-info">
                <h4>Nguồn phụ</h4>
                <p>Jikan & Kitsu</p>
              </div>
              <div class="mini-btns">
                <button @click="handleSeed('jikan')" :disabled="seeding !== null">Top Jikan</button>
                <button @click="handleSeed('kitsu')" :disabled="seeding !== null">Top Kitsu</button>
              </div>
            </div>
          </div>
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
  padding-bottom: 40px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-title {
  font-size: 32px;
  font-weight: 900;
  letter-spacing: -0.03em;
  color: #fff;
  margin: 0;
}
.page-subtitle {
  font-size: 14px;
  color: #a1a1aa;
  margin-top: 4px;
}
.header-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(34, 197, 94, 0.1);
  border: 1px solid rgba(34, 197, 94, 0.2);
  border-radius: 12px;
  padding: 8px 16px;
  color: #4ade80;
  font-size: 13px;
  font-weight: 600;
}
.online-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4ade80;
  box-shadow: 0 0 12px #4ade80;
}

/* Stat Cards */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}
.stat-card {
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 24px;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}
.stat-card:hover {
  transform: translateY(-4px);
  border-color: rgba(255, 255, 255, 0.2);
  background: #1c1c1f;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.stat-label {
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #71717a;
}
.stat-value {
  font-size: 32px;
  font-weight: 900;
  color: #fff;
  margin: 8px 0;
  letter-spacing: -0.01em;
}
.stat-sub {
  font-size: 12px;
  color: #52525b;
}

.stat-icon-bg {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-icon-bg svg {
  width: 28px;
  height: 28px;
}

.stat-card--violet .stat-icon-bg {
  background: rgba(139, 92, 246, 0.1);
  color: #a78bfa;
}
.stat-card--emerald .stat-icon-bg {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
}
.stat-card--blue .stat-icon-bg {
  background: rgba(59, 130, 246, 0.1);
  color: #60a5fa;
}
.stat-card--sky .stat-icon-bg {
  background: rgba(14, 165, 233, 0.1);
  color: #38bdf8;
}
.stat-card--amber .stat-icon-bg {
  background: rgba(245, 158, 11, 0.1);
  color: #fbbf24;
}
.stat-card--rose .stat-icon-bg {
  background: rgba(244, 63, 94, 0.1);
  color: #fb7185;
}

.stat-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  font-weight: 600;
  color: #71717a;
}
.stat-footer svg {
  width: 14px;
  height: 14px;
  transition: transform 0.2s;
}
.stat-card:hover .stat-footer svg {
  transform: translateX(3px);
  color: #fff;
}

/* Seeder Section */
.seeder-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
}
.seeder-card.pro {
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.15), rgba(79, 70, 229, 0.05));
  border: 1px solid rgba(124, 58, 237, 0.3);
  border-radius: 20px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.seeder-main {
  display: flex;
  gap: 20px;
}
.seeder-icon.pro {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: #7c3aed;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 8px 24px rgba(124, 58, 237, 0.3);
}
.seeder-icon.pro svg {
  width: 32px;
  height: 32px;
}
.seeder-info h3 {
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  margin: 0;
}
.seeder-info p {
  font-size: 14px;
  color: #a1a1aa;
  margin-top: 8px;
  line-height: 1.5;
}

.seeder-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}
.btn-seed {
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}
.btn-seed.primary {
  background: #7c3aed;
  color: #fff;
}
.btn-seed.primary:hover:not(:disabled) {
  background: #6d28d9;
  transform: translateY(-1px);
}
.btn-seed.secondary {
  background: rgba(255, 255, 255, 0.05);
  color: #e4e4e7;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.btn-seed.secondary:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
}

.seeder-card-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.seeder-mini-card {
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 18px;
  padding: 16px;
}
.mini-info h4 {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  margin: 0;
}
.mini-info p {
  font-size: 12px;
  color: #71717a;
  margin: 4px 0 12px;
}
.mini-btns {
  display: flex;
  gap: 8px;
}
.mini-btns button {
  flex: 1;
  padding: 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #a1a1aa;
  cursor: pointer;
  transition: all 0.2s;
}
.mini-btns button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  border-color: rgba(255, 255, 255, 0.15);
}

/* Quick Management */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}
.quick-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 16px;
  background: #18181b;
  border: 1px solid rgba(255, 255, 255, 0.04);
  text-decoration: none;
  transition: all 0.2s;
}
.quick-card:hover {
  background: #1c1c1f;
  transform: scale(1.02);
  border-color: rgba(255, 255, 255, 0.1);
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
.quick-label {
  font-size: 14px;
  font-weight: 600;
  color: #e4e4e7;
}
.quick-desc {
  font-size: 11px;
  color: #52525b;
  margin-top: 1px;
}

.quick-card--violet .quick-icon {
  background: rgba(139, 92, 246, 0.1);
  color: #a78bfa;
}
.quick-card--emerald .quick-icon {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
}
.quick-card--sky .quick-icon {
  background: rgba(14, 165, 233, 0.1);
  color: #38bdf8;
}
.quick-card--orange .quick-icon {
  background: rgba(249, 115, 22, 0.1);
  color: #fb923c;
}
.quick-card--blue .quick-icon {
  background: rgba(59, 130, 246, 0.1);
  color: #60a5fa;
}
.quick-card--amber .quick-icon {
  background: rgba(245, 158, 11, 0.1);
  color: #fbbf24;
}
.quick-card--rose .quick-icon {
  background: rgba(244, 63, 94, 0.1);
  color: #fb7185;
}
.quick-card--zinc .quick-icon {
  background: rgba(113, 113, 122, 0.1);
  color: #a1a1aa;
}

/* Mini Stats */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 4px;
}
.update-time {
  font-size: 11px;
  color: #3f3f46;
  font-weight: 500;
}
.stats-mini-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}
.stat-card-mini {
  background: #111113;
  border: 1px solid rgba(255, 255, 255, 0.04);
  border-radius: 14px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.stat-card-mini.highlight {
  border-color: rgba(124, 58, 237, 0.2);
  background: rgba(124, 58, 237, 0.03);
}
.mini-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  font-size: 16px;
}
.stat-card-mini .stat-label {
  font-size: 11px;
  color: #52525b;
  font-weight: 600;
  text-transform: uppercase;
}
.stat-card-mini .stat-value {
  font-size: 20px;
  font-weight: 800;
  color: #e4e4e7;
  margin: 0;
}

.stat-skeleton {
  display: inline-block;
  width: 80px;
  height: 32px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  animation: pulse 1.5s infinite;
}
@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.section-title {
  font-size: 16px;
  font-weight: 800;
  color: #a1a1aa;
  margin-bottom: 20px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
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
.seeder-card.metadata {
  background: rgba(16, 185, 129, 0.05);
  border-color: rgba(16, 185, 129, 0.1);
}
.seeder-card.legacy {
  background: #18181b;
  border-color: rgba(255, 255, 255, 0.06);
}
.seeder-info h3 {
  margin: 0;
  font-size: 16px;
  color: #f4f4f5;
}
.seeder-info p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #71717a;
}

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
.seed-btn:hover:not(:disabled) {
  background: #6d28d9;
  transform: scale(1.02);
}
.seed-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.seed-btn.sm {
  padding: 6px 12px;
  font-size: 12px;
  background: #27272a;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.seed-btn.sm:hover {
  background: #3f3f46;
}

.flex {
  display: flex;
}
.gap-2 {
  gap: 8px;
}
</style>
