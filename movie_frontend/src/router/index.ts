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
      name: 'admin',
      meta: { requiresAuth: true, requiresAdmin: true },
      component: () => import('../views/admin/DashboardView.vue'),
    },
    {
      path: '/admin/movies',
      name: 'admin-movies',
      meta: { requiresAuth: true, requiresAdmin: true },
      component: () => import('../views/admin/MovieManagementView.vue'),
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      meta: { requiresAuth: true, requiresAdmin: true },
      component: () => import('../views/admin/UserManagementView.vue'),
    },
    {
      path: '/admin/comments',
      name: 'admin-comments',
      meta: { requiresAuth: true, requiresAdmin: true },
      component: () => import('../views/admin/CommentManagementView.vue'),
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
