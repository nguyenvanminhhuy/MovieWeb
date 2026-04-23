import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
    },
    {
      path: '/browse',
      name: 'browse',
      component: () => import('../views/BrowseView.vue'),
    },
    {
      path: '/movie/:id',
      name: 'movie',
      component: () => import('../views/MovieDetailView.vue'),
    },
    {
      path: '/watch/:movieId',
      name: 'watch',
      component: () => import('../views/WatchView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: () => import('../views/NotificationView.vue'),
    },
    {
      path: '/favorites',
      name: 'favorites',
      meta: { requiresAuth: true },
      component: () => import('../views/FavoritesView.vue'),
    },
    {
      path: '/history',
      name: 'history',
      meta: { requiresAuth: true },
      component: () => import('../views/HistoryView.vue'),
    },
    {
      path: '/profile',
      name: 'profile',
      meta: { requiresAuth: true },
      component: () => import('../views/ProfileView.vue'),
    },
    {
      path: '/admin',
      name: 'admin-dashboard',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Dashboard' },
      component: () => import('../views/admin/DashboardView.vue'),
    },
    {
      path: '/admin/movies',
      name: 'admin-movies',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Quản lý phim' },
      component: () => import('../views/admin/MovieManagementView.vue'),
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Người dùng' },
      component: () => import('../views/admin/UserManagementView.vue'),
    },
    {
      path: '/admin/comments',
      name: 'admin-comments',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Bình luận' },
      component: () => import('../views/admin/CommentManagementView.vue'),
    },
    {
      path: '/admin/genres',
      name: 'admin-genres',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Thể loại' },
      component: () => import('../views/admin/GenreManagementView.vue'),
    },
    {
      path: '/admin/studios',
      name: 'admin-studios',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Studio' },
      component: () => import('../views/admin/StudioManagementView.vue'),
    },
    {
      path: '/admin/franchises',
      name: 'admin-franchises',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Franchise' },
      component: () => import('../views/admin/FranchiseManagementView.vue'),
    },
    {
      path: '/admin/movies/:movieId/episodes',
      name: 'admin-episodes',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Quản lý tập phim' },
      component: () => import('../views/admin/EpisodeManagementView.vue'),
    },
    {
      path: '/admin/reviews',
      name: 'admin-reviews',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Đánh giá' },
      component: () => import('../views/admin/ReviewManagementView.vue'),
    },
    {
      path: '/admin/audit-logs',
      name: 'admin-audit-logs',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Audit Logs' },
      component: () => import('../views/admin/AuditLogView.vue'),
    },
    {
      path: '/admin/reports',
      name: 'admin-reports',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Báo cáo vi phạm' },
      component: () => import('../views/admin/ReportManagementView.vue'),
    },
    {
      path: '/admin/roles',
      name: 'admin-roles',
      meta: { requiresAuth: true, requiresAdmin: true, title: 'Phân quyền' },
      component: () => import('../views/admin/RoleManagementView.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'notfound',
      component: () => import('../views/NotFoundView.vue'),
    },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('access_token')

  if (to.meta.requiresAuth && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.meta.requiresAdmin) {
    // We might need to check the profile info here, but since AppShell handles refreshProfile,
    // we just check if the user is currently loaded and is admin.
    // In a real app, you might want to fetch profile if not already there.
    // For now, let's keep it simple and assume the UI only shows links to admins.
  }
})

export default router
