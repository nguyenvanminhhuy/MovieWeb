<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth'

const route = useRoute()
const router = useRouter()
const { user, logout } = useAuth()
const sidebarOpen = ref(true)

const navItems = [
  {
    group: 'Tổng quan',
    items: [
      { name: 'admin-dashboard', label: 'Dashboard', icon: 'dashboard', path: '/admin' },
    ],
  },
  {
    group: 'Nội dung',
    items: [
      { name: 'admin-movies', label: 'Quản lý phim', icon: 'movie', path: '/admin/movies' },
      { name: 'admin-genres', label: 'Thể loại', icon: 'genre', path: '/admin/genres' },
      { name: 'admin-studios', label: 'Studio', icon: 'studio', path: '/admin/studios' },
      { name: 'admin-franchises', label: 'Franchise', icon: 'franchise', path: '/admin/franchises' },
    ],
  },
  {
    group: 'Cộng đồng',
    items: [
      { name: 'admin-users', label: 'Người dùng', icon: 'users', path: '/admin/users' },
      { name: 'admin-comments', label: 'Bình luận', icon: 'comments', path: '/admin/comments' },
      { name: 'admin-reviews', label: 'Đánh giá', icon: 'reviews', path: '/admin/reviews' },
      { name: 'admin-reports', label: 'Báo cáo', icon: 'reports', path: '/admin/reports' },
    ],
  },
  {
    group: 'Hệ thống',
    items: [
      { name: 'admin-audit-logs', label: 'Nhật ký hệ thống', icon: 'audit', path: '/admin/audit-logs' },
      { name: 'admin-roles', label: 'Phân quyền', icon: 'roles', path: '/admin/roles' },
    ],
  },
]

function isActive(name: string) {
  return route.name === name
}

async function handleLogout() {
  await logout()
  router.push('/login')
}
</script>

<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside :class="['admin-sidebar', sidebarOpen ? 'open' : 'collapsed']">
      <!-- Logo -->
      <div class="sidebar-logo">
        <RouterLink to="/" class="logo-link">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M15 10L19.553 7.724C20.224 7.388 21 7.87 21 8.618v6.764c0 .748-.776 1.23-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span v-if="sidebarOpen" class="logo-text">AnimeStream</span>
        </RouterLink>
        <button class="toggle-btn" @click="sidebarOpen = !sidebarOpen">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path v-if="sidebarOpen" d="M11 19l-7-7 7-7M18 19l-7-7 7-7" stroke-linecap="round" stroke-linejoin="round"/>
            <path v-else d="M13 5l7 7-7 7M6 5l7 7-7 7" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>

      <!-- Admin badge -->
      <div v-if="sidebarOpen" class="admin-badge">
        <div class="admin-avatar">
          <img v-if="user?.avatar" :src="user.avatar" alt="avatar" />
          <span v-else>{{ (user?.username || 'A').charAt(0).toUpperCase() }}</span>
        </div>
        <div class="admin-info">
          <div class="admin-name">{{ user?.fullName || user?.username }}</div>
          <div class="admin-role">Administrator</div>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="sidebar-nav">
        <div v-for="group in navItems" :key="group.group" class="nav-group">
          <div v-if="sidebarOpen" class="nav-group-label">{{ group.group }}</div>
          <RouterLink
            v-for="item in group.items"
            :key="item.name"
            :to="item.path"
            :class="['nav-item', isActive(item.name) ? 'active' : '']"
            :title="!sidebarOpen ? item.label : ''"
          >
            <!-- Icons -->
            <span class="nav-icon">
              <!-- Dashboard -->
              <svg v-if="item.icon === 'dashboard'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="7" height="7" rx="1"/>
                <rect x="14" y="3" width="7" height="7" rx="1"/>
                <rect x="3" y="14" width="7" height="7" rx="1"/>
                <rect x="14" y="14" width="7" height="7" rx="1"/>
              </svg>
              <!-- Movie -->
              <svg v-else-if="item.icon === 'movie'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18"/>
                <line x1="7" y1="2" x2="7" y2="22"/><line x1="17" y1="2" x2="17" y2="22"/>
                <line x1="2" y1="12" x2="22" y2="12"/><line x1="2" y1="7" x2="7" y2="7"/>
                <line x1="2" y1="17" x2="7" y2="17"/><line x1="17" y1="17" x2="22" y2="17"/>
                <line x1="17" y1="7" x2="22" y2="7"/>
              </svg>
              <!-- Genre -->
              <svg v-else-if="item.icon === 'genre'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 6h16M4 12h16M4 18h7" stroke-linecap="round"/>
              </svg>
              <!-- Studio -->
              <svg v-else-if="item.icon === 'studio'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
              </svg>
              <!-- Franchise -->
              <svg v-else-if="item.icon === 'franchise'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/>
              </svg>
              <!-- Users -->
              <svg v-else-if="item.icon === 'users'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/>
              </svg>
              <!-- Reviews -->
              <svg v-else-if="item.icon === 'reviews'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
              </svg>
              <!-- Audit -->
              <svg v-else-if="item.icon === 'audit'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
              </svg>
              <!-- Reports -->
              <svg v-else-if="item.icon === 'reports'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" stroke-linecap="round"/>
              </svg>
              <!-- Roles -->
              <svg v-else-if="item.icon === 'roles'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 15V3m0 12l-4-4m4 4l4-4M2 17l.621 2.485A2 2 0 004.561 21h14.878a2 2 0 001.94-1.515L22 17" stroke-linecap="round"/>
              </svg>
              <!-- Comments -->
              <svg v-else-if="item.icon === 'comments'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
              </svg>
            </span>
            <span v-if="sidebarOpen" class="nav-label">{{ item.label }}</span>
            <span v-if="sidebarOpen && isActive(item.name)" class="nav-active-dot"></span>
          </RouterLink>
        </div>
      </nav>

      <!-- Bottom actions -->
      <div class="sidebar-bottom">
        <RouterLink to="/" :class="['nav-item', 'text-zinc-500']" title="Về trang chủ">
          <span class="nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
            </svg>
          </span>
          <span v-if="sidebarOpen">Về trang chủ</span>
        </RouterLink>
        <button @click="handleLogout" class="nav-item logout-btn">
          <span class="nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
          <span v-if="sidebarOpen">Đăng xuất</span>
        </button>
      </div>
    </aside>

    <!-- Main content area -->
    <div class="admin-content">
      <!-- Top bar -->
      <header class="admin-topbar">
        <div class="topbar-left">
          <button class="mobile-menu-btn" @click="sidebarOpen = !sidebarOpen">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="3" y1="6" x2="21" y2="6" stroke-linecap="round"/>
              <line x1="3" y1="12" x2="21" y2="12" stroke-linecap="round"/>
              <line x1="3" y1="18" x2="21" y2="18" stroke-linecap="round"/>
            </svg>
          </button>
          <div class="breadcrumb">
            <span class="breadcrumb-admin">Admin</span>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">{{ route.meta?.title || route.name }}</span>
          </div>
        </div>
        <div class="topbar-right">
          <div class="topbar-user">
            <div class="user-avatar-sm">
              <img v-if="user?.avatar" :src="user.avatar" alt="" />
              <span v-else>{{ (user?.username || 'A').charAt(0).toUpperCase() }}</span>
            </div>
            <span class="user-name-sm">{{ user?.username }}</span>
          </div>
        </div>
      </header>

      <!-- Page content -->
      <main class="admin-main">
        <slot />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #09090b;
  color: #f4f4f5;
  font-family: 'Inter', system-ui, sans-serif;
}

/* ===== SIDEBAR ===== */
.admin-sidebar {
  display: flex;
  flex-direction: column;
  background: #111113;
  border-right: 1px solid rgba(255,255,255,0.06);
  transition: width 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 50;
  overflow: hidden;
}
.admin-sidebar.open { width: 240px; }
.admin-sidebar.collapsed { width: 64px; }

.sidebar-logo {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 12px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  min-height: 64px;
}
.logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: inherit;
}
.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}
.logo-icon svg { width: 20px; height: 20px; }
.logo-text {
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(to right, #a78bfa, #e879f9);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  white-space: nowrap;
}
.toggle-btn {
  background: none;
  border: none;
  color: #71717a;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  display: flex;
  transition: color 0.2s, background 0.2s;
  flex-shrink: 0;
}
.toggle-btn:hover { color: #f4f4f5; background: rgba(255,255,255,0.06); }
.toggle-btn svg { width: 16px; height: 16px; }

.admin-badge {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  margin: 8px;
  border-radius: 12px;
  background: rgba(124,58,237,0.1);
  border: 1px solid rgba(124,58,237,0.2);
}
.admin-avatar {
  width: 36px; height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 14px; color: white;
  flex-shrink: 0; overflow: hidden;
}
.admin-avatar img { width: 100%; height: 100%; object-fit: cover; }
.admin-info { overflow: hidden; }
.admin-name { font-size: 13px; font-weight: 600; color: #e4e4e7; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.admin-role { font-size: 11px; color: #a78bfa; font-weight: 500; }

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  scrollbar-width: none;
}
.sidebar-nav::-webkit-scrollbar { display: none; }

.nav-group { margin-bottom: 4px; }
.nav-group-label {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #52525b;
  padding: 12px 10px 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 10px;
  text-decoration: none;
  color: #71717a;
  font-size: 13.5px;
  font-weight: 500;
  transition: all 0.15s;
  position: relative;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  white-space: nowrap;
}
.nav-item:hover { color: #f4f4f5; background: rgba(255,255,255,0.05); }
.nav-item.active { color: #a78bfa; background: rgba(124,58,237,0.12); }

.nav-icon {
  width: 20px; height: 20px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.nav-icon svg { width: 18px; height: 18px; }
.nav-label { flex: 1; }
.nav-active-dot {
  width: 6px; height: 6px;
  background: #a78bfa;
  border-radius: 50%;
}

.sidebar-bottom {
  padding: 8px;
  border-top: 1px solid rgba(255,255,255,0.06);
}
.logout-btn { color: #ef4444 !important; }
.logout-btn:hover { background: rgba(239,68,68,0.1) !important; color: #f87171 !important; }

/* ===== MAIN CONTENT ===== */
.admin-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.admin-sidebar.open ~ .admin-content { margin-left: 240px; }
.admin-sidebar.collapsed ~ .admin-content { margin-left: 64px; }

.admin-topbar {
  position: sticky;
  top: 0;
  z-index: 40;
  background: rgba(9,9,11,0.9);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}
.topbar-left { display: flex; align-items: center; gap: 12px; }
.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  color: #71717a;
  cursor: pointer;
  padding: 6px;
  border-radius: 8px;
  transition: all 0.2s;
}
.mobile-menu-btn:hover { color: #f4f4f5; background: rgba(255,255,255,0.08); }
.mobile-menu-btn svg { width: 20px; height: 20px; }

.breadcrumb { display: flex; align-items: center; gap: 6px; }
.breadcrumb-admin { font-size: 13px; color: #52525b; }
.breadcrumb-sep { color: #3f3f46; }
.breadcrumb-current { font-size: 13px; font-weight: 600; color: #a1a1aa; text-transform: capitalize; }

.topbar-right { display: flex; align-items: center; gap: 12px; }
.topbar-user { display: flex; align-items: center; gap: 8px; }
.user-avatar-sm {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700; color: white;
  overflow: hidden;
}
.user-avatar-sm img { width: 100%; height: 100%; object-fit: cover; }
.user-name-sm { font-size: 13px; color: #a1a1aa; font-weight: 500; }

.admin-main {
  flex: 1;
  padding: 28px 24px;
  overflow: auto;
}

@media (max-width: 768px) {
  .mobile-menu-btn { display: flex; }
  .admin-sidebar { transform: translateX(-100%); }
  .admin-sidebar.open { transform: translateX(0); width: 240px; }
  .admin-sidebar.open ~ .admin-content,
  .admin-sidebar.collapsed ~ .admin-content { margin-left: 0; }
}
</style>
