import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue' // 稍后创建

const routes = [
    {
        path: '/login',
        name: 'Login',
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
                name: 'Dashboard',
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
                name: 'GoodsList',
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
                name: 'CategoryList',
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
                name: 'SupplierList',
                component: () => import('../views/supplier/index.vue'),
                meta: { title: '供应商管理', icon: 'OfficeBuilding' }
            }
        ]
    },
    {
        path: '/record',
        component: Layout,
        children: [
            {
                path: 'list',
                name: 'RecordList',
                component: () => import('../views/record/index.vue'),
                meta: { title: '库存流水', icon: 'List' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
    const userStr = localStorage.getItem('user')
    if (to.path === '/login') {
        next()
    } else {
        if (!userStr) {
            next('/login')
        } else {
            next()
        }
    }
})

export default router
