<template>
  <div class="dashboard-page">
    <section class="hero">
      <div>
        <h2>经营看板</h2>
        <p>今天是 {{ currentDate }}，库存状态与经营指标实时更新。</p>
      </div>
      <el-button type="success" plain icon="Refresh" @click="loadDashboardData">刷新数据</el-button>
    </section>

    <el-row :gutter="18" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card" @click="handleCardClick(item.path)">
          <div class="stat-head">
            <span class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon><component :is="item.icon" /></el-icon>
            </span>
            <span class="stat-title">{{ item.title }}</span>
          </div>
          <div class="stat-value" :class="{ warn: item.isWarning && stats.warningCount > 0 }">
            {{ stats[item.key] || 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18" class="content-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>库存分类分布</span>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="shortcut-grid">
            <el-button type="primary" plain icon="Plus" @click="$router.push('/goods/list')">商品录入</el-button>
            <el-button type="success" plain icon="Top" @click="$router.push('/goods/list')">入库管理</el-button>
            <el-button type="warning" plain icon="Bottom" @click="$router.push('/goods/list')">出库管理</el-button>
            <el-button type="info" plain icon="OfficeBuilding" @click="$router.push('/supplier/list')">供应商列表</el-button>
          </div>
          <div class="assistant">
            <p><el-icon><InfoFilled /></el-icon> 库存低于预警值时，系统会在商品列表中高亮提示。</p>
            <p><el-icon><Calendar /></el-icon> 可在库存流水页面查看最近出入库详情。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="panel-card recent-card">
      <template #header>
        <div class="panel-header">
          <span>最近 5 条库存变动</span>
        </div>
      </template>
      <el-table :data="stats.recentRecords || []" border stripe>
        <el-table-column label="时间" prop="createTime" width="180">
          <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="商品名称" prop="goodsName" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'danger'">
              {{ scope.row.type === 1 ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数量" prop="qty" width="100" align="center" />
        <el-table-column label="备注" prop="remark" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getStats } from '@/api/dashboard'
import * as echarts from 'echarts'

const router = useRouter()
const stats = ref({})
const pieChartRef = ref(null)
const currentDate = new Date().toLocaleDateString()
let chartInstance = null

const statCards = [
  { title: '商品总数', key: 'goodsCount', icon: 'Goods', color: '#4fa3ff', path: '/goods/list' },
  { title: '今日销量', key: 'todaySales', icon: 'SoldOut', color: '#5cc07d', path: '/record/list' },
  { title: '库存预警', key: 'warningCount', icon: 'Warning', color: '#f56c6c', path: '/goods/list', isWarning: true },
  { title: '合作伙伴', key: 'supplierCount', icon: 'OfficeBuilding', color: '#8e97ab', path: '/supplier/list' }
]

const loadDashboardData = async () => {
  try {
    const res = await getStats()
    stats.value = res.data || {}
    renderChart(stats.value.pieChartData || [])
  } catch (error) {
    console.error('获取首页数据失败', error)
  }
}

const renderChart = chartData => {
  nextTick(() => {
    if (!pieChartRef.value) return
    if (!chartInstance) {
      chartInstance = echarts.init(pieChartRef.value)
      window.addEventListener('resize', () => chartInstance && chartInstance.resize())
    }
    chartInstance.setOption({
      tooltip: { trigger: 'item', formatter: '{a}<br/>{b}: {c} ({d}%)' },
      legend: { bottom: '2%', left: 'center' },
      color: ['#4fa3ff', '#5cc07d', '#f5a623', '#8e97ab', '#f56c6c', '#6b7fd7'],
      series: [
        {
          name: '库存分布',
          type: 'pie',
          radius: ['38%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
          data: chartData
        }
      ]
    })
  })
}

const handleCardClick = path => {
  router.push(path)
}

const formatTime = timeStr => {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard-page {
  min-height: calc(100vh - 96px);
  background:
    radial-gradient(circle at 90% -20%, rgba(92, 192, 125, 0.18), transparent 36%),
    radial-gradient(circle at -10% 20%, rgba(79, 163, 255, 0.22), transparent 34%),
    linear-gradient(180deg, #f5f9f6 0%, #eef3fb 100%);
  border-radius: 14px;
  padding: 16px;
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(6px);
  border-radius: 12px;
  padding: 12px 16px;
}

.hero h2 {
  margin: 0;
  font-size: 20px;
  color: #2f3f58;
}

.hero p {
  margin: 6px 0 0;
  color: #627086;
  font-size: 13px;
}

.stats-row {
  margin-top: 14px;
}

.stat-card {
  cursor: pointer;
  border-radius: 12px;
}

.stat-head {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.stat-title {
  font-size: 15px;
  color: #445066;
}

.stat-value {
  margin-top: 10px;
  font-size: 34px;
  font-weight: 700;
  color: #1f2d3d;
}

.stat-value.warn {
  color: #e24d4d;
}

.content-row {
  margin-top: 14px;
}

.panel-card {
  border-radius: 12px;
  border: 1px solid #e8eef6;
}

.panel-header {
  font-size: 15px;
  color: #344257;
  font-weight: 600;
}

.chart-box {
  height: 360px;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.shortcut-grid .el-button {
  height: 44px;
  margin-left: 0;
}

.assistant {
  margin-top: 16px;
  background: #f4f7fc;
  border-radius: 10px;
  padding: 12px;
  color: #58657b;
  font-size: 13px;
}

.assistant p {
  margin: 6px 0;
  display: flex;
  align-items: center;
}

.assistant .el-icon {
  margin-right: 6px;
  color: #4fa3ff;
}

.recent-card {
  margin-top: 14px;
}

@media (max-width: 992px) {
  .hero {
    align-items: flex-start;
    gap: 12px;
    flex-direction: column;
  }

  .shortcut-grid {
    grid-template-columns: 1fr;
  }
}
</style>
