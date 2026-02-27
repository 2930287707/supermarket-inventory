// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: 'http://localhost:8080', // 确保这里的端口和你后端的端口一致
  timeout: 5000
})

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果后端返回的结构是 {code: 200, data: ...}，我们直接把 res 返回出去
    return res
  },
  error => {
    console.error('API Error:', error)
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default service
