<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
      <div class="logo">
        <el-icon size="24" color="#fff" style="vertical-align: middle; margin-right: 8px;"><FilledShop /></el-icon>
        <span v-if="!isCollapse">超市管理</span>
      </div>
      <el-menu
        :default-active="activePath"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :collapse="isCollapse"
        router
        class="el-menu-vertical"
      >
        <!-- 我们直接根据路由配置里的 path 来跳转 -->
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
        <el-menu-item index="/record/list">
          <el-icon><List /></el-icon>
          <template #title>库存流水</template>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主体区域 -->
    <div class="main-container">
      <!-- 顶部 Header -->
      <div class="navbar">
        <div class="left-nav">
          <el-icon class="hamburger" @click="toggleSidebar">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <span class="breadcrumb-text">{{ currentTitle }}</span>
        </div>
        <div class="right-menu">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="avatar-wrapper">
              <el-avatar shape="square" :size="30" style="background: #409EFF; margin-right: 8px;">{{ userNick.charAt(0) }}</el-avatar>
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
      </div>

      <!-- 内容显示区 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const userStr = localStorage.getItem('user')
const userObj = userStr ? JSON.parse(userStr) : {}
const userNick = userObj.nickname || 'Admin'

const activePath = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '首页')

const toggleSidebar = () => {
    isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
    if (command === 'logout') {
        localStorage.removeItem('user')
        router.push('/login')
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
  width: 210px;
  background-color: #304156;
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
  height: 50px;
  line-height: 50px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  background-color: #2b2f3a;
  overflow: hidden;
  white-space: nowrap;
}

.el-menu-vertical {
  border: none;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background-color: #f0f2f5;
}

.navbar {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
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
  color: #606266;
}

.right-menu {
  cursor: pointer;
}
.avatar-wrapper {
  display: flex;
  align-items: center;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
