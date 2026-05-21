<template>
  <div class="app-container">
    <!-- 1. 顶部搜索栏 -->
    <el-card shadow="never" class="filter-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="queryParams.keyword" 
            placeholder="搜索商品名称或条码" 
            clearable 
            prefix-icon="Search"
            @input="handleFilter"
          />
        </el-col>
        <el-col :span="4">
          <el-select 
            v-model="queryParams.category" 
            placeholder="全部分类" 
            clearable 
            @change="handleFilter"
            style="width: 100%"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.name"
            />
          </el-select>
        </el-col>
        <el-col :span="14" style="text-align: right">
          <el-button type="warning" plain icon="Warning" @click="toggleWarningMode">
            {{ isWarningMode ? '显示全部' : '只看缺货' }}
          </el-button>
          <el-button :disabled="!canEdit" type="primary" icon="Plus" @click="openGoodsDialog()">新增商品</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 2. 商品表格 -->
    <el-card shadow="never" class="table-container">
      <el-table 
        :data="pagedData" 
        border 
        stripe 
        v-loading="loading" 
        style="width: 100%"
        height="calc(100vh - 280px)"
      >
        <el-table-column prop="barcode" label="条形码" width="140" fixed />
        <el-table-column prop="name" label="商品名称" min-width="160" show-overflow-tooltip />
        
        <el-table-column prop="category" label="分类" width="110" align="center">
          <template #default="scope">
            <el-tag effect="plain" round>{{ scope.row.category }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="spec" label="规格" width="100" show-overflow-tooltip />
        
        <el-table-column label="价格信息" width="160" align="center">
          <template #default="scope">
            <div class="price-cell">
              <span class="badg">进</span> ¥{{ scope.row.priceIn }}
              <el-divider direction="vertical" />
              <span class="badg out">售</span> <span class="price-out">¥{{ scope.row.priceOut }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="stockCurrent" label="库存状态" width="140" align="center">
          <template #default="scope">
             <el-tag :type="scope.row.stockCurrent < scope.row.stockWarning ? 'danger' : 'success'">
                {{ scope.row.stockCurrent }}
             </el-tag>
             <span class="stock-sub" v-if="scope.row.stockCurrent < scope.row.stockWarning">
               (缺 {{ scope.row.stockWarning - scope.row.stockCurrent }})
             </span>
          </template>
        </el-table-column>

        <el-table-column label="快捷操作" width="160" align="center">
           <template #default="scope">
              <el-button-group>
                <el-button size="small" type="success" plain @click="openStockDialog(scope.row, 1)">入库</el-button>
                <el-button size="small" type="warning" plain @click="openStockDialog(scope.row, 2)">出库</el-button>
              </el-button-group>
           </template>
        </el-table-column>

        <el-table-column label="管理" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button :disabled="!canEdit" link type="primary" icon="Edit" @click="openGoodsDialog(scope.row)"></el-button>
            <el-popconfirm :disabled="!canDelete" title="确定删除吗？" width="220" @confirm="handleDelete(scope.row.id)">
               <template #reference>
                 <el-button :disabled="!canDelete" link type="danger" icon="Delete"></el-button>
               </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
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

    <!-- 弹窗1：新增/编辑商品 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="goodsForm.id ? '编辑商品' : '录入新商品'" 
      width="600px" 
      append-to-body
      @closed="resetGoodsForm"
    >
      <el-form ref="goodsFormRef" :model="goodsForm" :rules="goodsRules" label-width="90px">
        <el-form-item label="条形码" prop="barcode">
          <el-input v-model="goodsForm.barcode" placeholder="请扫描或输入" :disabled="!!goodsForm.id" />
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="goodsForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-row>
           <el-col :span="12">
              <el-form-item label="分类" prop="category">
                <el-select v-model="goodsForm.category" placeholder="请选择" style="width:100%">
                   <el-option v-for="c in categoryOptions" :key="c.id" :label="c.name" :value="c.name" />
                </el-select>
              </el-form-item>
           </el-col>
           <el-col :span="12">
              <el-form-item label="规格" prop="spec">
                <el-input v-model="goodsForm.spec" placeholder="如: 500ml/瓶" />
              </el-form-item>
           </el-col>
        </el-row>
        <el-row>
           <el-col :span="12">
              <el-form-item label="进货价" prop="priceIn">
                 <el-input-number v-model="goodsForm.priceIn" :precision="2" :step="0.1" :min="0" style="width:100%"/>
              </el-form-item>
           </el-col>
           <el-col :span="12">
              <el-form-item label="销售价" prop="priceOut">
                 <el-input-number v-model="goodsForm.priceOut" :precision="2" :step="0.1" :min="0" style="width:100%"/>
              </el-form-item>
           </el-col>
        </el-row>
        <el-row>
           <el-col :span="12">
              <el-form-item label="库存预警" prop="stockWarning">
                 <el-input-number v-model="goodsForm.stockWarning" :min="0" style="width:100%"/>
              </el-form-item>
           </el-col>
           <el-col :span="12" v-if="!goodsForm.id">
              <el-form-item label="初始库存" prop="stockCurrent">
                 <el-input-number v-model="goodsForm.stockCurrent" :min="0" style="width:100%"/>
              </el-form-item>
           </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGoods" :loading="btnLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 弹窗2：快捷入口选择商品 -->
    <el-dialog
      v-model="stockSelectDialogVisible"
      :title="stockType === 1 ? '选择入库商品' : '选择出库商品'"
      width="760px"
      append-to-body
    >
      <el-input
        v-model="stockSelectKeyword"
        placeholder="搜索商品名称或条码"
        clearable
        prefix-icon="Search"
        style="margin-bottom: 12px"
      />
      <el-table
        :key="stockSelectKeyword"
        :data="stockSelectData"
        border
        stripe
        height="360px"
        style="width: 100%"
      >
        <el-table-column prop="barcode" label="条形码" width="140" />
        <el-table-column prop="name" label="商品名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" align="center" />
        <el-table-column prop="spec" label="规格" width="100" show-overflow-tooltip />
        <el-table-column prop="stockCurrent" label="当前库存" width="100" align="center" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              :type="stockType === 1 ? 'success' : 'warning'"
              @click="selectStockGoods(scope.row)"
            >
              选择
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 弹窗3：快速出入库 -->
    <el-dialog 
      v-model="stockDialogVisible" 
      :title="stockType === 1 ? '🛒 快速入库' : '📤 快速出库'" 
      width="400px"
      append-to-body
    >
      <div class="stock-info">
        <h3>{{ currentStockItem.name }}</h3>
        <p>当前库存: <strong>{{ currentStockItem.stockCurrent }}</strong></p>
      </div>
      <el-form :model="stockForm" label-position="top">
        <el-form-item :label="stockType === 1 ? '入库数量' : '出库数量'">
           <el-input-number v-model="stockForm.qty" :min="1" size="large" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注说明">
           <el-input v-model="stockForm.remark" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button :type="stockType === 1 ? 'success' : 'warning'" @click="submitStock" :loading="btnLoading">
          确认
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getGoodsList, addGoods, updateGoods, deleteGoods } from '@/api/goods'
import { getCategoryList } from '@/api/category'
import { operateStock } from '@/api/stock'
import { ElMessage } from 'element-plus'
import { hasAnyRole } from '@/utils/auth'

// === 核心数据 ===
const loading = ref(false)
const btnLoading = ref(false)
const fullData = ref([])        
const filteredData = ref([])    
const categoryOptions = ref([]) 
const isWarningMode = ref(false) 
const canEdit = hasAnyRole(['ADMIN'])
const canDelete = hasAnyRole(['ADMIN'])
const route = useRoute()
const router = useRouter()

// === 分页配置 ===
const currentPage = ref(1)
const pageSize = ref(10)
const pagedData = computed(() => {
  // 确保 filteredData 始终是数组，防止报错
  const list = Array.isArray(filteredData.value) ? filteredData.value : []
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return list.slice(start, end)
})

const stockSelectData = computed(() => {
  const list = Array.isArray(fullData.value) ? fullData.value : []
  const keyword = stockSelectKeyword.value.trim().toLowerCase()
  if (!keyword) return list
  return list.filter(item =>
    String(item.name || '').toLowerCase().includes(keyword) ||
    String(item.barcode || '').toLowerCase().includes(keyword) ||
    String(item.category || '').toLowerCase().includes(keyword) ||
    String(item.spec || '').toLowerCase().includes(keyword)
  )
})

const queryParams = reactive({
  keyword: '',
  category: ''
})

// === 表单数据 ===
const dialogVisible = ref(false)
const goodsFormRef = ref(null)
const goodsForm = ref({
  id: null, barcode: '', name: '', category: '', spec: '', 
  priceIn: 0, priceOut: 0, stockCurrent: 0, stockWarning: 10
})
const goodsRules = {
  barcode: [{ required: true, message: '必填', trigger: 'blur' }],
  name: [{ required: true, message: '必填', trigger: 'blur' }],
  category: [{ required: true, message: '必选', trigger: 'change' }]
}

// === 库存弹窗 ===
const stockSelectDialogVisible = ref(false)
const stockSelectKeyword = ref('')
const stockDialogVisible = ref(false)
const stockType = ref(1)
const currentStockItem = ref({})
const stockForm = ref({ qty: 1, remark: '' })
const currentOperatorId = 1 


const initData = async () => {
  loading.value = true
  try {
    // 修复：后端 /goods/list 默认只返回 pageSize=10 条，
    // 而前端是"拉全量 + 本地分页/筛选"模式，必须传一个大的 pageSize 把全部商品拉回来。
    const [goodsRes, catRes] = await Promise.all([
      getGoodsList({ pageNum: 1, pageSize: 9999 }),
      getCategoryList()
    ])
    
    // 1. 处理【商品数据】
    // 你的日志显示：Goods -> code:200, data: { total:5, list: [...] }
    // 所以层级是：goodsRes.data (拿到body) -> .data (拿到payload) -> .list (拿到数组)
    if (goodsRes.data && goodsRes.data.data && goodsRes.data.data.list) {
       fullData.value = goodsRes.data.data.list
       console.log('✅ 商品数据加载成功，数量:', fullData.value.length)
    } else {
       // 备用：万一拦截器已经解包了一层
       fullData.value = goodsRes.data?.list || []
    }

    // 2. 处理【分类数据】
    // 你的日志显示：Category -> code:200, data: [ ... ]
    // 所以层级是：catRes.data (拿到body) -> .data (拿到数组)
    if (catRes.data && Array.isArray(catRes.data.data)) {
       categoryOptions.value = catRes.data.data
       console.log('✅ 分类数据加载成功，数量:', categoryOptions.value.length)
    } else {
       // 备用
       categoryOptions.value = catRes.data || []
    }

    if (route.query.warning === '1') {
      isWarningMode.value = true
    }
    handleFilter() // 初始化显示
    openStockDialogFromRoute()
    if (route.query.warning === '1') {
      router.replace('/goods/list')
    }

  } catch (e) {
    console.error('加载出错:', e)
    ElMessage.error('无法连接服务器')
  } finally {
    loading.value = false
  }
}

// === 过滤逻辑 ===
const handleFilter = () => {
  const source = Array.isArray(fullData.value) ? fullData.value : []
  let temp = [...source]

  if (isWarningMode.value) {
    temp = temp.filter(item => item.stockCurrent < item.stockWarning)
  }
  if (queryParams.category) {
    temp = temp.filter(item => item.category === queryParams.category)
  }
  if (queryParams.keyword) {
    const kw = queryParams.keyword.toLowerCase()
    temp = temp.filter(item => 
      (item.name && item.name.toLowerCase().includes(kw)) || 
      (item.barcode && String(item.barcode).includes(kw))
    )
  }

  filteredData.value = temp
  currentPage.value = 1
}

const toggleWarningMode = () => {
  isWarningMode.value = !isWarningMode.value
  handleFilter()
}

const openStockDialogFromRoute = () => {
  const stockTypeParam = route.query.stockType
  if (stockTypeParam !== 'in' && stockTypeParam !== 'out') return
  if (!filteredData.value.length) {
    ElMessage.warning('暂无商品可操作')
    return
  }
  stockType.value = stockTypeParam === 'in' ? 1 : 2
  stockSelectKeyword.value = ''
  stockSelectDialogVisible.value = true
  router.replace('/goods/list')
}

const selectStockGoods = (row) => {
  stockSelectDialogVisible.value = false
  openStockDialog(row, stockType.value)
}

// === 商品增删改 ===
const openGoodsDialog = (row) => {
  if (row) {
    goodsForm.value = { ...row }
  } else {
    // 新增
    goodsForm.value = {
      id: null, barcode: '', name: '', category: '', spec: '', 
      priceIn: 0, priceOut: 0, stockCurrent: 0, stockWarning: 10
    }
  }
  dialogVisible.value = true
}

const submitGoods = async () => {
  if (!goodsFormRef.value) return
  await goodsFormRef.value.validate(async (valid) => {
    if (valid) {
      btnLoading.value = true
      try {
        if (goodsForm.value.id) {
          await updateGoods(goodsForm.value)
          ElMessage.success('更新成功')
        } else {
          await addGoods(goodsForm.value)
          ElMessage.success('录入成功')
        }
        dialogVisible.value = false
        // 重新加载数据
        initData()
      } catch (e) {
        ElMessage.error(e.response?.data?.msg || '操作失败')
      } finally {
        btnLoading.value = false
      }
    }
  })
}

const handleDelete = async (id) => {
  try {
    await deleteGoods(id)
    ElMessage.success('删除成功')
    initData() // 重新加载
  } catch(e) {
    ElMessage.error('删除失败')
  }
}

const resetGoodsForm = () => {
   goodsFormRef.value?.resetFields()
}

// === 库存操作 ===
const openStockDialog = (row, type) => {
  currentStockItem.value = row
  stockType.value = type
  stockForm.value = { qty: 1, remark: '' }
  stockDialogVisible.value = true
}

const submitStock = async () => {
  btnLoading.value = true
  try {
    const payload = {
      goodId: currentStockItem.value.id,
      type: stockType.value,
      qty: stockForm.value.qty,
      remark: stockForm.value.remark,
      operatorId: currentOperatorId
    }
    await operateStock(payload)
    ElMessage.success('库存更新成功')
    stockDialogVisible.value = false
    
    // 乐观更新表格, 避免全量刷新闪烁
    const target = fullData.value.find(g => g.id === currentStockItem.value.id)
    if (target) {
      if (stockType.value === 1) target.stockCurrent += stockForm.value.qty
      else target.stockCurrent -= stockForm.value.qty
    }
    handleFilter()

  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '操作失败')
  } finally {
    btnLoading.value = false
  }
}

// === 分页事件 ===
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}
const handleCurrentChange = (val) => {
  currentPage.value = val
}

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
.table-container { padding-bottom: 50px; }

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.price-cell { font-size: 13px; }
.badg { font-size: 10px; padding: 1px 3px; background: #e1f3d8; color: #67c23a; border-radius: 2px; margin-right: 2px;}
.badg.out { background: #fde2e2; color: #f56c6c; }
.price-out { font-weight: bold; color: #f56c6c; }

.stock-sub { font-size: 12px; color: #f56c6c; margin-left: 5px; }
.stock-info {
  text-align: center;
  background: #f4f4f5;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.stock-info h3 { margin: 0 0 5px 0; color: #303133; }
.stock-info p { margin: 0; color: #606266; }
</style>
