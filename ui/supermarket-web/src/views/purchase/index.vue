<template>
  <div class="page-container">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="采购单号">
          <el-input v-model="query.orderNo" placeholder="请输入采购单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待审核" :value="0" />
            <el-option label="已入库" :value="1" />
            <el-option label="已作废" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button icon="Refresh" @click="reset">重置</el-button>
          <el-button :disabled="!canEdit" type="success" icon="Plus" @click="openCreateDialog">新建采购单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="tableData.list" border stripe style="width: 100%">
        <el-table-column type="index" width="60" label="序号" align="center" />
        <el-table-column prop="orderNo" label="采购单号" width="200" />
        <el-table-column prop="supplierName" label="供应商" width="180" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.status)">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right" />
        <el-table-column prop="creator" label="创建人" width="120" />
        <el-table-column prop="auditBy" label="审核人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="auditTime" label="审核时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="showDetail(scope.row)">详情</el-button>
            <el-button
              :disabled="!canEdit || scope.row.status !== 0"
              link
              type="success"
              @click="approve(scope.row)"
            >
              审核入库
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager-wrap">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="tableData.total"
          :page-sizes="[10, 20, 50]"
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新建采购单" width="900px" append-to-body>
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="供应商">
          <el-select v-model="createForm.supplierId" placeholder="请选择供应商" style="width: 360px">
            <el-option v-for="item in supplierOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" placeholder="选填" />
        </el-form-item>
      </el-form>

      <div class="item-toolbar">
        <span>采购明细</span>
        <el-button type="primary" plain size="small" icon="Plus" @click="addItem">添加商品</el-button>
      </div>

      <el-table :data="createForm.items" border>
        <el-table-column label="商品" min-width="260">
          <template #default="scope">
            <el-select
              v-model="scope.row.goodsId"
              placeholder="请选择商品"
              filterable
              style="width: 100%"
              @change="onGoodsChange(scope.row)"
            >
              <el-option
                v-for="g in goodsOptions"
                :key="g.id"
                :label="`${g.name}（${g.barcode}）`"
                :value="g.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="140">
          <template #default="scope">
            <el-input-number v-model="scope.row.qty" :min="1" @change="recalcRow(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="采购单价" width="170">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.price"
              :min="0"
              :step="0.1"
              :precision="2"
              @change="recalcRow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="金额" width="140" align="right">
          <template #default="scope">
            {{ formatMoney(scope.row.amount || 0) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="scope">
            <el-button link type="danger" icon="Delete" @click="removeItem(scope.$index)" />
          </template>
        </el-table-column>
      </el-table>

      <div class="total-line">
        合计金额：<span>{{ formatMoney(totalAmount) }}</span>
      </div>

      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitCreate">提交采购单</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="采购单详情" width="860px" append-to-body>
      <div v-if="detailData.order" class="detail-header">
        <p>采购单号：{{ detailData.order.orderNo }}</p>
        <p>供应商：{{ detailData.order.supplierName }}</p>
        <p>状态：{{ statusText(detailData.order.status) }}</p>
      </div>
      <el-table :data="detailData.items || []" border stripe>
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="goodsName" label="商品名称" min-width="180" />
        <el-table-column prop="qty" label="数量" width="120" align="center" />
        <el-table-column prop="price" label="采购单价" width="140" align="right" />
        <el-table-column prop="amount" label="金额" width="140" align="right" />
      </el-table>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { hasAnyRole } from '@/utils/auth'
import { getSupplierList } from '@/api/supplier'
import { getGoodsList } from '@/api/goods'
import {
  approvePurchaseOrder,
  createPurchaseOrder,
  getPurchaseDetail,
  getPurchasePage
} from '@/api/purchase'

const canEdit = hasAnyRole(['ADMIN', 'PURCHASER'])
const loading = ref(false)
const submitLoading = ref(false)
const supplierOptions = ref([])
const goodsOptions = ref([])

const tableData = ref({
  list: [],
  total: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  status: null
})

const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const detailData = ref({ order: null, items: [] })

const createForm = reactive({
  supplierId: null,
  remark: '',
  items: []
})

const totalAmount = computed(() =>
  createForm.items.reduce((sum, item) => sum + Number(item.amount || 0), 0)
)

const fetchBaseOptions = async () => {
  const [supplierRes, goodsRes] = await Promise.all([
    getSupplierList(),
    getGoodsList({ pageNum: 1, pageSize: 500 })
  ])
  supplierOptions.value = supplierRes.data || []
  goodsOptions.value = goodsRes.data?.list || []
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPurchasePage(query)
    tableData.value = res.data || { list: [], total: 0 }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '加载采购单失败')
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  fetchData()
}

const reset = () => {
  query.pageNum = 1
  query.pageSize = 10
  query.orderNo = ''
  query.status = null
  fetchData()
}

const statusText = status => {
  if (status === 0) return '待审核'
  if (status === 1) return '已入库'
  if (status === 2) return '已作废'
  return '未知'
}

const statusTagType = status => {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  return ''
}

const formatMoney = val => Number(val || 0).toFixed(2)

const openCreateDialog = () => {
  createForm.supplierId = null
  createForm.remark = ''
  createForm.items = []
  addItem()
  createDialogVisible.value = true
}

const addItem = () => {
  createForm.items.push({
    goodsId: null,
    qty: 1,
    price: 0,
    amount: 0
  })
}

const removeItem = index => {
  createForm.items.splice(index, 1)
}

const onGoodsChange = row => {
  const goods = goodsOptions.value.find(g => g.id === row.goodsId)
  if (goods && goods.priceIn != null) {
    row.price = Number(goods.priceIn)
  }
  recalcRow(row)
}

const recalcRow = row => {
  const qty = Number(row.qty || 0)
  const price = Number(row.price || 0)
  row.amount = Number((qty * price).toFixed(2))
}

const submitCreate = async () => {
  if (!createForm.supplierId) {
    ElMessage.warning('请选择供应商')
    return
  }
  if (!createForm.items.length) {
    ElMessage.warning('请至少添加一条采购明细')
    return
  }
  const invalid = createForm.items.some(item => !item.goodsId || !item.qty || item.qty <= 0)
  if (invalid) {
    ElMessage.warning('请完善采购明细中的商品和数量')
    return
  }

  submitLoading.value = true
  try {
    const payload = {
      supplierId: createForm.supplierId,
      remark: createForm.remark,
      items: createForm.items.map(item => ({
        goodsId: item.goodsId,
        qty: item.qty,
        price: item.price
      }))
    }
    await createPurchaseOrder(payload)
    ElMessage.success('采购单创建成功')
    createDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '创建采购单失败')
  } finally {
    submitLoading.value = false
  }
}

const showDetail = async row => {
  try {
    const res = await getPurchaseDetail(row.id)
    detailData.value = res.data || { order: null, items: [] }
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '加载详情失败')
  }
}

const approve = row => {
  ElMessageBox.confirm(`确认将采购单【${row.orderNo}】审核并入库吗？`, '确认操作', {
    type: 'warning'
  }).then(async () => {
    await approvePurchaseOrder(row.id)
    ElMessage.success('审核入库成功')
    fetchData()
  }).catch(() => {})
}

onMounted(async () => {
  try {
    await fetchBaseOptions()
  } catch (error) {
    ElMessage.error('基础数据加载失败，请刷新重试')
  }
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.filter-card {
  margin-bottom: 14px;
}

.item-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 0;
  font-weight: 600;
  color: #4b5565;
}

.total-line {
  text-align: right;
  margin-top: 12px;
  color: #4b5565;
}

.total-line span {
  font-size: 20px;
  font-weight: 700;
  color: #1f2d3d;
}

.pager-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.detail-header {
  display: flex;
  gap: 28px;
  margin-bottom: 10px;
  color: #4b5565;
}
</style>
