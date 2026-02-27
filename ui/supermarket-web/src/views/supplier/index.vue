<template>
  <div class="supplier-container">
    <!-- 顶部操作区 -->
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar-content">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增供应商</el-button>
        <el-button icon="Refresh" @click="fetchData" :loading="loading">刷新</el-button>
      </div>
    </el-card>

    <!-- 数据表格区 -->
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
        <el-table-column prop="name" label="供应商名称" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span class="supplier-name">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系人" width="120" align="center" />
        <el-table-column prop="phone" label="联系电话" width="150" align="center" />
        <el-table-column prop="address" label="详细地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="合作时间" width="180" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <!-- 操作栏 -->
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm
              title="确定要停止与该供应商的关联吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(scope.row.id)"
            >
              <template #reference>
                <el-button link type="danger" icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑供应商信息' : '录入新供应商'"
      width="550px"
      @closed="resetForm"
      append-to-body
    >
      <el-form
        ref="supplierFormRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入供应商全称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入对接人姓名" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号或座机号" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="3"
            placeholder="详细到门牌号"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">
            保 存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getSupplierList, 
  addSupplier, 
  updateSupplier, 
  deleteSupplier 
} from '@/api/supplier'

// === 响应式状态 ===
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const supplierFormRef = ref(null)

const form = ref({
  id: null,
  name: '',
  contact: '',
  phone: '',
  address: ''
})

// === 表单校验规则 ===
const rules = reactive({
  name: [
    { required: true, message: '供应商名称必填', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '联系人必填', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '联系电话必填', trigger: 'blur' },
    { 
      pattern: /^(1[3-9]\d{9}|0\d{2,3}-?\d{7,8})$/, 
      message: '请输入正确的手机号或座机格式', 
      trigger: 'blur' 
    }
  ],
  address: [
    { required: true, message: '地址必填', trigger: 'blur' }
  ]
})

// === 核心逻辑 ===

// 1. 获取列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSupplierList()
    // 之前后端代码返回的是 List<Supplier>，所以直接取 res.data
    tableData.value = res.data || []
  } catch (error) {
    console.error('Fetch error:', error)
    ElMessage.error('无法加载供应商数据')
  } finally {
    loading.value = false
  }
}

// 2. 新增按钮
const handleAdd = () => {
  form.value = { id: null, name: '', contact: '', phone: '', address: '' }
  dialogVisible.value = true
}

// 3. 编辑按钮
const handleEdit = (row) => {
  // 使用解构快速赋值，避免对象引用
  form.value = { ...row }
  dialogVisible.value = true
}

// 4. 提交表单（正式环境必须做参数校验）
const submitForm = async () => {
  if (!supplierFormRef.value) return
  
  await supplierFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.value.id) {
          await updateSupplier(form.value)
          ElMessage.success('更新成功')
        } else {
          await addSupplier(form.value)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 5. 删除操作
const handleDelete = async (id) => {
  try {
    await deleteSupplier(id)
    ElMessage.success('已解除合作关系')
    fetchData()
  } catch (error) {
    ElMessage.error('删除失败，可能存在业务关联')
  }
}

// 6. 重置表单状态
const resetForm = () => {
  if (supplierFormRef.value) {
    supplierFormRef.value.resetFields()
  }
}

// 辅助函数：格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.supplier-container {
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
.supplier-name {
  font-weight: 600;
  color: #409eff;
}
.toolbar-content {
  display: flex;
  justify-content: flex-start;
  gap: 12px;
}
.dialog-footer {
  padding-top: 20px;
}
:deep(.el-table__header) {
  background-color: #f8f9fb;
}
</style>