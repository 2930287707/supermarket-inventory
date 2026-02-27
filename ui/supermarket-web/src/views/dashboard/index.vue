<template>
  <div class="dashboard-container">
    <!-- 1. 顶部数据卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card" @click="handleCardClick(item.path)">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-number" :class="{ 'warning-text': item.isWarning && stats.warningCount > 0 }">
                {{ stats[item.key] || 0 }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 中间图表与快捷方式 -->
    <el-row :gutter="20" style="margin-top: 20px;" class="chart-row">
  <el-col :span="16">
    <el-card shadow="hover" header="库存分类分布" class="full-height-card">
      <div ref="pieChartRef" style="height: 350px;"></div>
    </el-card>
  </el-col>
  <el-col :span="8">
    <el-card shadow="hover" header="快捷入口" class="full-height-card">
      <!-- 快捷入口容器 -->
      <div class="shortcut-container">
        <div class="shortcut-grid">
          <el-button type="primary" plain icon="Plus" @click="$router.push('/goods/list')">商品录入</el-button>
          <el-button type="success" plain icon="Top" @click="$router.push('/goods/list')">入库管理</el-button>
          <el-button type="warning" plain icon="Bottom" @click="$router.push('/goods/list')">出库管理</el-button>
          <el-button type="info" plain icon="User" @click="$router.push('/supplier/list')">供应商列表</el-button>
        </div>
        
        <div class="system-assistant">
          <el-divider content-position="left">系统助手</el-divider>
          <div class="helper-content">
            <p><el-icon><InfoFilled /></el-icon> 提示：库存低于预警值时，数值将变红。</p>
            <p><el-icon><Calendar /></el-icon> 当前日期：{{ currentDate }}</p>
          </div>
        </div>
      </div>
    </el-card>
  </el-col>
</el-row>

    <!-- 3. 底部最近活动 -->
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover" header="最近 5 笔库存变动记录">
          <el-table :data="stats.recentRecords" border stripe>
            <el-table-column label="变动时间" prop="createTime" width="180">
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
            <el-table-column label="操作备注" prop="remark" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getStats } from '@/api/dashboard'
import * as echarts from 'echarts'

const router = useRouter()
const stats = ref({})
const pieChartRef = ref(null)
const currentDate = new Date().toLocaleDateString()

// 定义卡片配置
const statCards = [
  { title: '商品总数', key: 'goodsCount', icon: 'Goods', color: '#409EFF', path: '/goods/list' },
  { title: '今日销售', key: 'todaySales', icon: 'SoldOut', color: '#67C23A', path: '/record/list' },
  { title: '库存预警', key: 'warningCount', icon: 'Warning', color: '#F56C6C', path: '/goods/list', isWarning: true },
  { title: '合作伙伴', key: 'supplierCount', icon: 'OfficeBuilding', color: '#909399', path: '/supplier/list' }
]

// 接口调用
const loadDashboardData = async () => {
  try {
    const res = await getStats()
    stats.value = res.data
    initChart(res.data.pieChartData)
  } catch (error) {
    console.error('获取首页数据失败', error)
  }
}

// 初始化饼图
const initChart = (chartData) => {
  if (!chartData) return
  nextTick(() => {
    const myChart = echarts.init(pieChartRef.value)
    const option = {
      tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
      legend: { bottom: '0%', left: 'center' },
      series: [
        {
          name: '分类库存分布',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          emphasis: { label: { show: true, fontSize: '18', fontWeight: 'bold' } },
          data: chartData
        }
      ]
    }
    myChart.setOption(option)
    // 窗口缩放自适应
    window.addEventListener('resize', () => myChart.resize())
  })
}

const handleCardClick = (path) => {
  router.push(path)
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
/* 整个容器的内边距 */
.dashboard-container {
  padding: 0;
}
/* 强制 Row 中的 Col 等高 */
.chart-row {
  display: flex;
  align-items: stretch;
}
.chart-row .el-col {
  display: flex;
}
/* 强制 Card 撑满高度 */
.full-height-card {
  width: 100%;
  display: flex;
  flex-direction: column;
}
/* 让 Card 的 body 也撑满，这样内容才能垂直拉伸 */
:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}
/* 快捷入口容器：上下分布 */
.shortcut-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* 关键：让按钮区和助手区分别在上下两头 */
}
.shortcut-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-top: 10px;
}
/* 放大按钮效果 */
.shortcut-grid .el-button {
  height: 50px;
  font-size: 15px;
  margin-left: 0 !important; /* 修复 el-button 默认左间距 */
}
/* 系统助手区 */
.system-assistant {
  margin-top: auto; /* 保证它停留在底部 */
  padding-bottom: 10px;
}
.helper-content {
  font-size: 14px;
  color: #606266;
  background: #f8f9fb;
  padding: 15px;
  border-radius: 8px;
  line-height: 2;
}
.helper-content .el-icon {
  vertical-align: middle;
  margin-right: 5px;
  color: #409eff;
}
/* 统计卡片样式保持不变... */
.stat-card { cursor: pointer; transition: transform 0.3s; }
.stat-card:hover { transform: translateY(-5px); }
.stat-content { display: flex; align-items: center; }
.stat-info { margin-left: 15px; }
.stat-number { font-size: 24px; font-weight: bold; }
.warning-text { color: #F56C6C; }
</style>
