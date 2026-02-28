<template>
  <div class="login-page">
    <div class="bg-shape shape-left"></div>
    <div class="bg-shape shape-right"></div>
    <div class="login-panel">
      <div class="brand">
        <h1>小型超市库存管理系统</h1>
        <p>毕业设计演示版</p>
      </div>
      <el-form :model="loginForm" label-position="top" size="large" @keyup.enter="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-button class="login-btn" type="primary" :loading="loading" @click="handleLogin">登录系统</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginForm = ref({ username: '', password: '' })
const loading = ref(false)

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await login(loginForm.value)
    const loginData = res.data || {}
    if (!loginData.token) {
      ElMessage.error('登录失败：未获取到令牌')
      return
    }

    localStorage.setItem('token', loginData.token)
    localStorage.setItem('user', JSON.stringify(loginData.user || {}))
    ElMessage.success('登录成功')
    await router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(140deg, #e6efe9 0%, #dbe7f3 55%, #f3efe4 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  padding: 16px;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(6px);
  z-index: 0;
}

.shape-left {
  width: 360px;
  height: 360px;
  left: -120px;
  top: -80px;
  background: radial-gradient(circle, rgba(43, 123, 117, 0.35) 0%, rgba(43, 123, 117, 0) 70%);
}

.shape-right {
  width: 420px;
  height: 420px;
  right: -150px;
  bottom: -140px;
  background: radial-gradient(circle, rgba(232, 145, 74, 0.35) 0%, rgba(232, 145, 74, 0) 70%);
}

.login-panel {
  position: relative;
  z-index: 1;
  width: 440px;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.65);
  border-radius: 20px;
  padding: 34px 30px;
  box-shadow: 0 14px 38px rgba(38, 47, 66, 0.18);
}

.brand {
  margin-bottom: 20px;
  text-align: center;
}

.brand h1 {
  margin: 0;
  font-size: 24px;
  color: #233044;
  letter-spacing: 1px;
}

.brand p {
  margin: 8px 0 0;
  color: #5e6b82;
  font-size: 13px;
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  margin-top: 8px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .login-panel {
    padding: 26px 18px;
    border-radius: 14px;
  }

  .brand h1 {
    font-size: 20px;
  }
}
</style>
