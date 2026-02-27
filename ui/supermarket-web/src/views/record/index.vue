<template>
  <div class="app-container">
    <!-- 1. 顶部筛选栏 -->
    <el-card shadow="never" class="filter-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="queryParams.keyword" 
            placeholder="搜索商品名称 / 条码 / 备注" 
            clearable 
            prefix-icon="Search"
            @input="handleFilter"
          />
        </el-col>
        <el-col :span="4">
          <el-select 
            v-model="queryParams.type" 
            placeholder="全部类型" 
            clearable 
            @change="handleFilter"
            style="width: 100%"
          >
            <el-option label="入库 (采购/退货)" :value="1" />
            <el-option label="出库 (销售/报损)" :value="2" />
          </el-select>
        </el-col>
        <!-- 右侧按钮：刷新 -->
        <el-col :span="14" style="text-align: right">
          <el-button type="primary" plain icon="Refresh" @click="initData">刷新记录</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 2. 数据表格 -->
    <el-card shadow="never" class="table-container">
      <el-table 
        :data="pagedData" 
        border 
        stripe 
        v-loading="loading" 
        style="width: 100%"
        height="calc(100vh - 250px)"
      >
        <!-- 操作时间 -->
        <el-table-column prop="createTime" label="操作时间" width="180" sortable>
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <!-- 类型：入库/出库 -->
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'warning'" effect="dark">
              {{ scope.row.type === 1 ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 商品信息 -->
        <el-table-column prop="goodsName" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="barcode" label="条形码" width="150" />

        <!-- 变动数量 -->
        <el-table-column prop="qty" label="变动数量" width="120" align="center">
          <template #default="scope">
            <span 
              :style="{ 
                color: scope.row.type === 1 ? '#67c23a' : '#e6a23c', 
                fontWeight: 'bold',
                fontSize: '16px' 
              }"
            >
              {{ scope.row.type === 1 ? '+' : '-' }}{{ scope.row.qty }}
            </span>
          </template>
        </el-table-column>

        <!-- 操作人 (暂时 Mock Admin) -->
        <el-table-column label="操作员" width="120" align="center">
          <template #default>
            <el-tag type="info" size="small">管理员</el-tag>
          </template>
        </el-table-column>

        <!-- 备注 -->
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.remark">{{ scope.row.remark }}</span>
            <span v-else style="color: #ccc">无</span>
          </template>
        </el-table-column>

      </el-table>

      <!-- 3. 前端分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="filteredData.length"
          :page-sizes="[10, 20, 50, 100]"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getStockList } from '@/api/stock'
import { ElMessage } from 'element-plus'

// === 数据定义 ===
const loading = ref(false)
const fullData = ref([])     // 原始全部数据
const filteredData = ref([]) // 筛选后的数据

const currentPage = ref(1)
const pageSize = ref(10)

const queryParams = reactive({
  keyword: '',
  type: null // null=全部, 1=入库, 2=出库
})

// === 前端分页计算 ===
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

// === 时间格式化工具 ===
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  return new Date(timeStr).toLocaleString()
}

// === 获取数据 ===
const initData = async () => {
  loading.value = true
  try {
    const res = await getStockList()
    
    // 兼容处理：确保拿到的总是数组
    // 假设后端返回结构是 { code: 200, data: [...] } 或 { code: 200, data: { list: [...] } }
    let list = []
    if (res.data && Array.isArray(res.data)) {
        list = res.data
    } else if (res.data && res.data.list && Array.isArray(res.data.list)) {
        list = res.data.list
    } else if (Array.isArray(res)) {
        list = res
    }
    
    fullData.value = list
    handleFilter() // 加载完立即执行一次筛选

  } catch (e) {
    console.error(e)
    ElMessage.error('获取记录失败')
  } finally {
    loading.value = false
  }
}

// === 核心筛选逻辑 (纯前端实现) ===
const handleFilter = () => {
  let temp = [...fullData.value]

  // 1. 类型筛选
  if (queryParams.type) {
    temp = temp.filter(item => item.type === queryParams.type)
  }

  // 2. 关键词模糊搜索
  if (queryParams.keyword) {
    const kw = queryParams.keyword.toLowerCase()
    temp = temp.filter(item => {
      const name = item.goodsName ? item.goodsName.toLowerCase() : ''
      const code = item.barcode ? String(item.barcode) : ''
      const remark = item.remark ? item.remark.toLowerCase() : ''
      return name.includes(kw) || code.includes(kw) || remark.includes(kw)
    })
  }

  filteredData.value = temp
  currentPage.value = 1 // 搜索后重置页码
}

// === 分页事件 ===
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}
const handleCurrentChange = (val) => {
  currentPage.value = val
}

// === 启动 ===
onMounted(() => {
  initData()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}
.filter-container { margin-bottom: 15px; }
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>