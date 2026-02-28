<template>
  <div class="app-container">
    <!-- 1. 顶部操作栏 -->
    <el-card shadow="never" class="toolbar">
      <el-button :disabled="!canEdit" type="primary" icon="Plus" @click="openDialog()">新增分类</el-button>
      <el-button icon="Refresh" @click="fetchData">刷新</el-button>
      <div class="tips">
        <el-icon><InfoFilled /></el-icon>
        <span>提示：分类排序值越小，显示越靠前；若分类下存在商品，必须先处理商品才能删除分类。</span>
      </div>
    </el-card>

    <!-- 2. 数据表格 -->
    <el-card shadow="never" class="table-wrapper">
      <el-table 
        :data="tableData" 
        border 
        stripe
        v-loading="loading" 
        style="width: 100%"
        height="calc(100vh - 200px)"
      >
        <el-table-column prop="sortOrder" label="排序" width="100" align="center" sortable />
        <el-table-column prop="name" label="分类名称" min-width="200">
           <template #default="scope">
             <span style="font-weight: bold; color: #303133;">{{ scope.row.name }}</span>
           </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button :disabled="!canEdit" link type="primary" icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
            <el-popconfirm 
              :disabled="!canDelete"
              title="确定删除此分类吗？" 
              confirm-button-text="强制删除" 
              cancel-button-text="取消"
              @confirm="handleDelete(scope.row.id)"
              width="220"
            >
              <template #reference>
                <el-button :disabled="!canDelete" link type="danger" icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 3. 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="form.id ? '编辑分类' : '新增分类'" 
      width="400px"
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：酒水饮料" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width: 100%" />
          <div class="form-tip">数字越小越靠前 (0-999)</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="btnLoading" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'
import { ElMessage } from 'element-plus'
import { hasAnyRole } from '@/utils/auth'

// === 状态定义 ===
const loading = ref(false)
const btnLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const canEdit = hasAnyRole(['ADMIN', 'MANAGER'])
const canDelete = hasAnyRole(['ADMIN'])

const form = ref({
  id: null,
  name: '',
  sortOrder: 0
})

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

// === 方法定义 ===

// 1. 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    // 你的后端 Category list 接口返回的是直接的 List，所以通常 Res.data 就是数组
    // 做个简单的兼容判断
    if (res.data && Array.isArray(res.data)) {
        tableData.value = res.data
    } else {
        tableData.value = []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

// 2. 打开弹窗
const openDialog = (row) => {
  if (row) {
    form.value = { ...row } // 克隆对象，防止修改表格时直接变动
  } else {
    form.value = { id: null, name: '', sortOrder: 0 }
  }
  dialogVisible.value = true
}

// 3. 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      btnLoading.value = true
      try {
        if (form.value.id) {
          await updateCategory(form.value)
          ElMessage.success('更新成功')
        } else {
          await addCategory(form.value)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchData() // 刷新列表
      } catch (error) {
        // 捕获后端抛出的异常（如：分类名重复）
        const msg = error.response?.data?.msg || error.msg || '操作失败'
        ElMessage.error(msg)
      } finally {
        btnLoading.value = false
      }
    }
  })
}

// 4. 删除
const handleDelete = async (id) => {
  try {
    await deleteCategory(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    // 捕获后端抛出的“该分类下有商品”异常
    const msg = error.response?.data?.msg || error.msg || '删除失败'
    ElMessage.error(msg)
  }
}

// 工具：格式化时间
const formatTime = (isoString) => {
  if (!isoString) return ''
  return isoString.replace('T', ' ').substring(0, 19)
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f0f2f5;
  height: 100vh;
  box-sizing: border-box;
}
.toolbar {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}
.tips {
  display: inline-flex;
  align-items: center;
  margin-left: 20px;
  color: #909399;
  font-size: 13px;
  background: #f4f4f5;
  padding: 5px 10px;
  border-radius: 4px;
}
.tips .el-icon {
  margin-right: 5px;
}
.form-tip {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
  margin-top: 5px;
}
</style>
