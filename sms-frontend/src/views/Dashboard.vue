<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in coreData" :key="item.title">
        <el-card class="stat-card">
          <div class="stat-title">{{ item.title }}</div>
          <div class="stat-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">月度订单与营收趋势</div>
          </template>
          <div ref="monthlyChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">订单状态占比</div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import request from '../utils/request'
import {ElMessage} from 'element-plus'
import {ref, onMounted, onUnmounted} from 'vue'

//核心指标
const coreData = ref([
  {title: '累计客户数', value: 0},
  {title: '累计订单数', value: 0},
  {title: '本月营收', value: '0 元'},
  {title: '订单完成率', value: '0%'}
])

const monthlyChartRef = ref(null)
const pieChartRef = ref(null)

let monthlyChart = null
let pieChart = null

//获取核心指标数据
const getCoreData = async () => {
  try {
    const data = await request.get('/api/stats/core')
    coreData.value = [
      {title: '累计客户数', value: data.totalCustomer},
      {title: '累计订单数', value: data.totalOrder},
      {title: '本月营收', value: `${data.monthAmount} 元`},
      {title: '订单完成率', value: data.accomplishedRate}
    ]
  } catch (e) {
    console.error(e)
    ElMessage.error('获取统计数据失败，' + (e.response?.data?.message || e.message))
  }
}

//绘制月度趋势图
const renderMonthlyChart = async () => {
  if (!monthlyChart) return
  monthlyChart.showLoading()

  try {
    const data = await request.get('/api/stats/monthly', {params: {year: new Date().getFullYear()}}) || []

    monthlyChart.setOption({
      tooltip: {trigger: 'axis'},
      legend: {data: ['订单数量', '营收金额'], bottom: 0},
      //增加边距防止标签切割
      grid: {top: 40, bottom: 60, left: 60, right: 60},
      xAxis: {
        type: 'category',
        data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map(n => n + '月')
      },
      yAxis: [
        {type: 'value', name: '订单数量'},
        {type: 'value', name: '营收金额', position: 'right'}
      ],
      series: [
        {
          name: '订单数量',
          type: 'bar',
          data: data.map(item => item?.orderCount),
          itemStyle: {borderRadius: [4, 4, 0, 0]}
        },
        {
          name: '营收金额',
          type: 'line',
          yAxisIndex: 1,
          data: data.map(item => item?.amount),
          smooth: true
        }
      ]
    }, true)//不合并，强制刷新
  } catch (e) {
    console.error(e)
    ElMessage.error('获取趋势图失败，' + (e.response?.data?.message || e.message))
  } finally {
    monthlyChart.hideLoading()
  }
}

//绘制饼图
const renderPieChart = async () => {
  if (!pieChart) return
  pieChart.showLoading()

  try {
    const data = await request.get('/api/stats/order')

    pieChart.setOption({
      tooltip: {trigger: 'item', formatter: '{b}: {c} ({d}%)'},
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['55%', '50%'],//稍微右移给图例留空间
          avoidLabelOverlap: true,
          label: {
            show: true,
            formatter: '{b}\n{d}%'
          },
          //高亮样式
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            },
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          labelLine: {
            show: true,
            length: 20,
            length2: 30
          },
          data: [
            {value: data[0], name: '已完成'},
            {value: data[1], name: '服务中'},
            {value: data[2], name: '待审核'},
            {value: data[3], name: '已取消'}
          ]
        }
      ]
    }, true)
  } catch (e) {
    console.error(e)
    ElMessage.error('获取订单比例图失败，' + (e.response?.data?.message || e.message))
  } finally {
    pieChart.hideLoading()
  }
}

const onResize = () => {
  monthlyChart?.resize()
  pieChart?.resize()
}


onMounted(() => {
  monthlyChart = echarts.init(monthlyChartRef.value)
  pieChart = echarts.init(pieChartRef.value)

  getCoreData()
  renderMonthlyChart()
  renderPieChart()

  window.addEventListener('resize', onResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
  if (monthlyChart) {
    monthlyChart.dispose()
    monthlyChart = null
  }
  if (pieChart) {
    pieChart.dispose()
    pieChart = null
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.stat-card {
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-title {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-value {
  color: #333;
  font-size: 32px;
  font-weight: bold;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}
</style>
