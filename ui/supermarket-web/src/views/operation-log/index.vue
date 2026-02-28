<template>
  <div class="page-container">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="操作人">
          <el-input v-model="query.username" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item label="操作名称">
          <el-input v-model="query.operation" placeholder="如：新增商品" clearable />
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="query.success" placeholder="全部" clearable style="width: 120px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button icon="Refresh" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="tableData.list" border stripe style="width: 100%">
        <el-table-column type="index" width="60" label="序号" align="center" />
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column prop="username" label="操作人" width="130" />
        <el-table-column prop="role" label="角色" width="110" />
        <el-table-column prop="operation" label="操作名称" min-width="150" />
        <el-table-column prop="requestPath" label="请求路径" min-width="220" show-overflow-tooltip />
        <el-table-column label="结果" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.success === 1 ? 'success' : 'danger'">
              {{ scope.row.success === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costMs" label="耗时(ms)" width="110" align="center" />
      </el-table>

      <div class="pager-wrap">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="tableData.total"
          :page-sizes="[10, 20, 50, 100]"
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getOperationLogPage } from '@/api/operationLog'

const loading = ref(false)
const tableData = ref({
  list: [],
  total: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  operation: '',
  success: null
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOperationLogPage(query)
    tableData.value = res.data || { list: [], total: 0 }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '加载日志失败')
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
  query.username = ''
  query.operation = ''
  query.success = null
  fetchData()
}

onMounted(() => {
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

.table-card {
  border: none;
}

.pager-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>
