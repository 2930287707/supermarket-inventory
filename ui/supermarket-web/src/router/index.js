import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const routes = [
  {
    path: '/login',
    name: '登录',
    component: () => import('../views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: '首页概览',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '首页概览', icon: 'Odometer' }
      }
    ]
  },
  {
    path: '/goods',
    component: Layout,
    redirect: '/goods/list',
    children: [
      {
        path: 'list',
        name: '商品管理',
        component: () => import('../views/goods/index.vue'),
        meta: { title: '商品管理', icon: 'Goods' }
      }
    ]
  },
  {
    path: '/category',
    component: Layout,
    children: [
      {
        path: 'list',
        name: '分类管理',
        component: () => import('../views/category/index.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      }
    ]
  },
  {
    path: '/supplier',
    component: Layout,
    children: [
      {
        path: 'list',
        name: '供应商管理',
        component: () => import('../views/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'OfficeBuilding' }
      }
    ]
  },
  {
    path: '/purchase',
    component: Layout,
    children: [
      {
        path: 'list',
        name: '采购管理',
        component: () => import('../views/purchase/index.vue'),
        meta: { title: '采购管理', icon: 'ShoppingCart' }
      }
    ]
  },
  {
    path: '/record',
    component: Layout,
    children: [
      {
        path: 'list',
        name: '库存流水',
        component: () => import('../views/record/index.vue'),
        meta: { title: '库存流水', icon: 'List' }
      }
    ]
  },
  {
    path: '/operation-log',
    component: Layout,
    children: [
      {
        path: 'list',
        name: '操作日志',
        component: () => import('../views/operation-log/index.vue'),
        meta: { title: '操作日志', icon: 'Document' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
    return
  }

  if (!token) {
    next('/login')
    return
  }
  next()
})

export default router
