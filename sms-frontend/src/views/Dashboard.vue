<template>
  <div class="dashboard">
    <el-row :gutter="20" style="margin-bottom: 20px; align-items: center;">
      <el-col :span="4">
        <el-select
            v-model="selectedYear"
            placeholder="选择年份"
            @change="init"
            style="width: 100%;"
        >
          <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year ? `${year}年` : '全部'"
              :value="year"
          />
        </el-select>
      </el-col>
      <el-col :span="4">
        <el-button
            type="primary"
            :loading="exportLoading"
            @click="handleExport"
        >
          <i class="el-icon-download"></i> 导出年度报表
        </el-button>
      </el-col>
    </el-row>

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
            <div class="card-header">{{ selectedYear ? '月度' : '年度' }}订单与营收趋势</div>
          </template>
          <div ref="lineChartRef" class="chart-container"></div>
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
import * as xlsx from 'xlsx'
import * as echarts from 'echarts'
import request from '../utils/request'
import {ElMessage} from 'element-plus'
import {ref, onMounted, onUnmounted} from 'vue'

//核心指标
const coreData = ref([
  {title: '累计客户数', value: 0},
  {title: '累计订单数', value: 0},
  {title: '当年营收', value: '0 元'},
  {title: '订单完成率', value: '0%'}
])

const lineChartRef = ref(null)
const pieChartRef = ref(null)

let lineChart = null
let pieChart = null

const currentYear = new Date().getFullYear()
const startYear = currentYear - 4
const endYear = currentYear

const selectedYear = ref(currentYear)
const yearOptions = ref([])

const exportLoading = ref(false)

//获取核心指标数据
const getCoreData = async () => {
  try {
    const year = selectedYear.value
    const data = await request.get(`/api/stats/core${year ? `?year=${year}` : ''}`)
    coreData.value = [
      {title: '累计客户数', value: data.totalCustomer},
      {title: '累计订单数', value: data.totalOrder},
      {title: year ? '当年营收' : '总营收', value: `${data.amount} 元`},
      {title: '订单完成率', value: data.accomplishedRate}
    ]
  } catch (e) {
    console.error(e)
    ElMessage.error('获取统计数据失败，' + (e.response?.data?.message || e.message))
  }
}

//绘制趋势图（月度/年度）
const renderLineChart = async () => {
  if (!lineChart) return
  lineChart.showLoading()

  try {
    const year = selectedYear.value
    const data = year ? await request.get(`/api/stats/monthly?year=${year}`)
        : await request.get(`/api/stats/annually?start=${startYear}&end=${endYear}`)

    lineChart.setOption({
      tooltip: {trigger: 'axis'},
      legend: {data: ['订单数量', '营收金额'], bottom: 0},
      //增加边距防止标签切割
      grid: {top: 40, bottom: 60, left: 60, right: 60},
      xAxis: {
        type: 'category',
        data: year
            ? [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map(n => n + '月')
            : yearOptions.value.slice(0, yearOptions.value.length - 1).map(n => n + '年')
      },
      yAxis: [
        {type: 'value', name: '订单数量'},
        {type: 'value', name: '营收金额', position: 'right'}
      ],
      series: [
        {
          name: '订单数量',
          type: 'bar',
          data: data.map(item => item?.orderCount || 0),
          itemStyle: {borderRadius: [4, 4, 0, 0]}
        },
        {
          name: '营收金额',
          type: 'line',
          yAxisIndex: 1,
          data: data.map(item => item?.amount || 0),
          smooth: true
        }
      ]
    }, true)//不合并，强制刷新
  } catch (e) {
    console.error(e)
    ElMessage.error('获取趋势图失败，' + (e.response?.data?.message || e.message))
  } finally {
    lineChart.hideLoading()
  }
}

//绘制饼图
const renderPieChart = async () => {
  if (!pieChart) return
  pieChart.showLoading()

  try {
    const year = selectedYear.value
    const data = await request.get(`/api/stats/order${year ? `?year=${year}` : ''}`)

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
            {value: data[0] || 0, name: '已完成'},
            {value: data[1] || 0, name: '服务中'},
            {value: data[2] || 0, name: '待审核'},
            {value: data[3] || 0, name: '已取消'}
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

//生成年份选项列表
const getYearOptions = () => {
  //只显示指定范围内的年份
  for (let i = startYear; i <= endYear; i++) {
    yearOptions.value.push(i)
  }
  yearOptions.value.push(null)
}

//导出报表功能
const handleExport = async () => {
  try {
    const year = selectedYear.value
    const exportData = [
      ...coreData.value.map(item => ({'指标': item.title, '数值': item.value})),
      {'指标': '统计年份', '年份': year ? `${year}年` : '全部'}
    ]

    const worksheet = xlsx.utils.json_to_sheet(exportData)
    const workbook = xlsx.utils.book_new()
    xlsx.utils.book_append_sheet(workbook, worksheet, '年度统计')
    xlsx.writeFile(workbook, (year ? `${year}年度` : '企业') + '运营报表.xlsx')
    ElMessage.success('导出成功')
  } catch (e) {
    console.error(e)
    ElMessage.error('导出失败，' + (e.response?.data?.message || e.message))
  }
}

const onResize = () => {
  lineChart?.resize()
  pieChart?.resize()
}

const init = () => {
  getCoreData()
  renderLineChart()
  renderPieChart()
}

onMounted(() => {
  lineChart = echarts.init(lineChartRef.value)
  pieChart = echarts.init(pieChartRef.value)

  getYearOptions()
  init()

  window.addEventListener('resize', onResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
  lineChart?.dispose()
  lineChart = null
  pieChart?.dispose()
  pieChart = null
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
