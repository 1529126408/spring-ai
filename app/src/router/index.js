import { createRouter, createWebHistory } from 'vue-router'
import ChatInterface from '@/views/ChatInterface.vue'

const routes = [
  {
    path: '/',
    name: 'Chat',
    component: ChatInterface
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ left: 0, top: 0 }),
})

// 添加全局前置守卫
// router.beforeEach((to, from, next) => {
//   // 这里可以添加权限验证逻辑
//   const isAuthenticated = localStorage.getItem('token')
//
//   if (to.path !== '/login' && !isAuthenticated) {
//     // 如果用户未登录且目标路由不是登录页，则重定向到登录页
//     next('/login')
//   } else {
//     // 否则继续导航
//     next()
//   }
// })

export default router