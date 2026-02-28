<template>
  <div class="app-wrapper">
    <aside class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
      <div class="logo">
        <el-icon size="24" color="#fff" style="vertical-align: middle; margin-right: 8px"><FilledShop /></el-icon>
        <span v-if="!isCollapse">超市库存系统</span>
      </div>
      <el-menu
        :default-active="activePath"
        background-color="#2f3d52"
        text-color="#c8d2e0"
        active-text-color="#4fc08d"
        :collapse="isCollapse"
        router
        class="el-menu-vertical"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>首页概览</template>
        </el-menu-item>
        <el-menu-item index="/goods/list">
          <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
        <el-menu-item index="/category/list">
          <el-icon><Menu /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/supplier/list">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>供应商管理</template>
        </el-menu-item>
        <el-menu-item index="/purchase/list">
          <el-icon><ShoppingCart /></el-icon>
          <template #title>采购管理</template>
        </el-menu-item>
        <el-menu-item index="/record/list">
          <el-icon><List /></el-icon>
          <template #title>库存流水</template>
        </el-menu-item>
        <el-menu-item index="/operation-log/list" :disabled="!canViewOperationLog">
          <el-icon><Document /></el-icon>
          <template #title>操作日志</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <div class="main-container">
      <header class="navbar">
        <div class="left-nav">
          <el-icon class="hamburger" @click="toggleSidebar">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <span class="breadcrumb-text">{{ currentTitle }}</span>
        </div>
        <div class="right-menu">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="avatar-wrapper">
              <el-avatar shape="square" :size="30" style="background: #4fc08d; margin-right: 8px">
                {{ userNick.charAt(0) }}
              </el-avatar>
              <span>{{ userNick }}</span>
              <el-icon class="el-icon--right"><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { hasAnyRole } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const canViewOperationLog = hasAnyRole(['ADMIN', 'MANAGER'])

const userStr = localStorage.getItem('user')
const userObj = userStr ? JSON.parse(userStr) : {}
const userNick = userObj.nickname || userObj.username || '管理员'

const activePath = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '首页')

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async command => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    await router.push('/login')
  }
}
</script>

<style scoped>
.app-wrapper {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.sidebar-container {
  width: 220px;
  background: linear-gradient(180deg, #2f3d52 0%, #243142 100%);
  height: 100%;
  transition: width 0.28s;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}

.sidebar-container.is-collapse {
  width: 64px;
}

.logo {
  height: 52px;
  line-height: 52px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  background: rgba(16, 28, 42, 0.55);
  overflow: hidden;
  white-space: nowrap;
}

.el-menu-vertical {
  border: none;
  flex: 1;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: linear-gradient(180deg, #eef4f1 0%, #f3f6fb 100%);
}

.navbar {
  height: 52px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px;
}

.hamburger {
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
  vertical-align: middle;
}

.breadcrumb-text {
  font-weight: 600;
  color: #50607a;
}

.right-menu {
  cursor: pointer;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  color: #364152;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.25s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-18px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(18px);
}
</style>
