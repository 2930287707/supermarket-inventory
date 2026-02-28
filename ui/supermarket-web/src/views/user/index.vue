<template>
  <div class="user-container">
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar-content">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增用户</el-button>
        <el-button icon="Refresh" :loading="loading" @click="fetchData">刷新</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        height="calc(100vh - 240px)"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="160" />
        <el-table-column prop="nickname" label="昵称" min-width="160" />
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="scope">
              <el-tag :type="roleTagType(scope.row.role)">{{ roleLabel(scope.row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="280" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              link
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm
              title="确认将密码重置为 123456 吗？"
              confirm-button-text="确认"
              cancel-button-text="取消"
              @confirm="handleResetPassword(scope.row)"
            >
              <template #reference>
                <el-button link type="danger">重置密码</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm
              title="确认删除该用户吗？删除后不可恢复。"
              confirm-button-text="确认"
              cancel-button-text="取消"
              @confirm="handleDelete(scope.row)"
            >
              <template #reference>
                <el-button link type="danger" :disabled="scope.row.username === 'admin'">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑用户' : '新增用户'"
      width="560px"
      @closed="resetForm"
      append-to-body
    >
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="110px" status-icon>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="仅支持字母、数字和下划线" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  addUser,
  deleteUser,
  getUserList,
  resetUserPassword,
  updateUser,
  updateUserStatus
} from '@/api/user'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const userFormRef = ref(null)

const roleOptions = [
  { value: 'ADMIN', label: '管理员' },
  { value: 'STAFF', label: '普通员工' },
  { value: 'PURCHASER', label: '进货上货员' },
  { value: 'ANALYST', label: '报表分析员' }
]

const form = ref({
  id: null,
  username: '',
  password: '',
  nickname: '',
  role: 'STAFF',
  status: 1
})

const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '长度应为 3 到 32 位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '仅支持字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 64, message: '长度应为 6 到 64 位', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 50, message: '长度不能超过 50 位', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserList()
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '用户列表加载失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {
    id: null,
    username: '',
    password: '',
    nickname: '',
    role: 'STAFF',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = row => {
  form.value = {
    id: row.id,
    username: row.username,
    password: '',
    nickname: row.nickname || '',
    role: row.role || 'STAFF',
    status: row.status == null ? 1 : row.status
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!userFormRef.value) return
  await userFormRef.value.validate(async valid => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (form.value.id) {
        await updateUser({
          id: form.value.id,
          nickname: form.value.nickname,
          role: form.value.role,
          status: form.value.status
        })
        ElMessage.success('用户更新成功')
      } else {
        await addUser({
          username: form.value.username,
          password: form.value.password,
          nickname: form.value.nickname,
          role: form.value.role,
          status: form.value.status
        })
        ElMessage.success('用户创建成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error(error.response?.data?.msg || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const toggleStatus = async row => {
  const nextStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus({ id: row.id, status: nextStatus })
    ElMessage.success(nextStatus === 1 ? '用户已启用' : '用户已禁用')
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '状态更新失败')
  }
}

const handleResetPassword = async row => {
  try {
    await resetUserPassword(row.id)
    ElMessage.success('密码已重置为 123456')
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '重置密码失败')
  }
}

const handleDelete = async row => {
  try {
    await deleteUser(row.id)
    ElMessage.success('用户删除成功')
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '删除用户失败')
  }
}

const resetForm = () => {
  userFormRef.value?.resetFields()
}

const roleTagType = role => {
  if (role === 'ADMIN') return 'danger'
  if (role === 'PURCHASER') return 'warning'
  if (role === 'ANALYST') return 'success'
  return 'info'
}

const roleLabel = role => {
  const found = roleOptions.find(item => item.value === role)
  return found ? found.label : role
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.toolbar-card {
  margin-bottom: 16px;
  border: none;
}

.table-card {
  border: none;
}

.toolbar-content {
  display: flex;
  justify-content: flex-start;
  gap: 12px;
}

:deep(.el-table__header) {
  background-color: #f8f9fb;
}
</style>
