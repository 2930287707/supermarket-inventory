<template>
  <div class="login-container">
    <div class="login-box">
      <h2>超市库存管理系统</h2>
      <el-form :model="loginForm" label-position="top" size="large">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="handleLogin">登录</el-button>
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

const handleLogin = () => {
    if(!loginForm.value.username || !loginForm.value.password) {
        ElMessage.error('请输入账号密码')
        return
    }
    loading.value = true
    login(loginForm.value).then(res => {
        // res.data 已经是 user 对象了（在 request.js 里处理过）
        localStorage.setItem('user', JSON.stringify(res.data))
        ElMessage.success('登录成功')
        router.push('/')
    }).catch(() => {
        loading.value = false
    })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background-color: #2d3a4b;
  display: flex;
  justify-content: center;
  align-items: center;
}
.login-box {
  background: white;
  width: 400px;
  padding: 40px;
  border-radius: 5px;
  text-align: center;
}
h2 { margin-bottom: 30px; color: #333; }
</style>
