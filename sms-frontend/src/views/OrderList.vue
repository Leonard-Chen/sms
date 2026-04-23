<template>
  <div class="order-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
        </div>
      </template>

      <div class="table-toolbar">
        <el-input
            v-model="keyword"
            placeholder="搜索：订单号/客户编号/服务类型/内容"
            clearable
            style="max-width: 340px"
        />
        <el-select v-model="statusFilter" clearable placeholder="状态" style="width: 140px">
          <el-option :value="1" label="待审核"/>
          <el-option :value="7" label="待分派"/>
          <el-option :value="8" label="待接单"/>
          <el-option :value="2" label="已生效"/>
          <el-option :value="3" label="服务中"/>
          <el-option :value="4" label="已完成"/>
          <el-option :value="5" label="已取消"/>
          <el-option :value="6" label="异常"/>
        </el-select>
        <el-button type="primary" @click="openCreateDialog">新增订单</el-button>
        <el-button type="primary" plain @click="showPendingSchedule">只看待分派</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <!-- 订单表格 -->
      <el-table
          :data="pagedData"
          border
          style="width: 100%"
          v-loading="loading"
          :empty-text="loading ? '加载中…' : '暂无数据'"
      >
        <el-table-column prop="orderNo" label="订单编号" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="customerNo" label="客户编号" min-width="140" show-overflow-tooltip/>
        <el-table-column prop="serviceType" label="服务类型" min-width="120" show-overflow-tooltip/>
        <el-table-column prop="serviceContent" label="服务内容" min-width="180" show-overflow-tooltip/>
        <el-table-column prop="orderAmount" label="订单金额" min-width="120">
          <template #default="{ row }">
            ¥{{ row.orderAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)">
              {{ getStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" show-overflow-tooltip/>
        <el-table-column label="操作" width="420" fixed="right">
          <template #default="{ row }">
            <!-- 审核按钮 -->
            <el-button
                v-if="row.orderStatus === 1"
                type="primary"
                size="small"
                @click="openAuditDialog(row)"
            >
              审核
            </el-button>

            <el-button
                v-if="row.orderStatus === 7 || row.orderStatus === 8"
                type="primary"
                plain
                size="small"
                @click="openScheduleDialog(row)"
            >
              {{ row.orderStatus === 8 ? '改派' : '分派' }}
            </el-button>

            <el-button
                v-if="row.orderStatus === 8"
                type="success"
                plain
                size="small"
                @click="handleAccept(row)"
            >
              接单
            </el-button>

            <el-button
                v-if="row.orderStatus === 3"
                type="success"
                size="small"
                @click="handleComplete(row)"
            >
              完成
            </el-button>

            <el-button
                v-if="[1, 7, 8].includes(row.orderStatus)"
                type="primary"
                plain
                size="small"
                @click="openEditDialog(row)"
            >
              修改
            </el-button>

            <!-- 查看详情 -->
            <el-button type="info" size="small" @click="viewDetail(row)">
              详情
            </el-button>

            <!-- 删除按钮 -->
            <el-button
                type="danger"
                size="small"
                @click="handleDelete(row.orderNo)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <div class="pager-meta">共 {{ filteredData.length }} 条</div>
        <el-select v-model="pageSize" style="width: 120px" size="small">
          <el-option :value="10" label="10 条/页"/>
          <el-option :value="20" label="20 条/页"/>
          <el-option :value="50" label="50 条/页"/>
          <el-option :value="100" label="100 条/页"/>
        </el-select>
        <el-pagination
            v-model:current-page="page"
            layout="prev, pager, next"
            :total="filteredData.length"
            background
            small
        />
      </div>
    </el-card>

    <!-- 【新增订单】或【编辑订单】弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增员工' : '编辑员工'" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单编号" v-if="dialogVisible === 'edit'">
          <el-input v-model="editForm.orderNo" disabled/>
        </el-form-item>
        <el-form-item label="选择客户" required>
          <el-select
              v-model="form.customerNo"
              filterable
              clearable
              placeholder="请选择客户（可搜索编号/姓名/电话）"
              style="width: 100%"
              :loading="customerLoading"
          >
            <el-option
                v-for="c in customerOptions"
                :key="c.customerNo"
                :label="`${c.customerNo} - ${c.customerName || ''}${c.contactPhone ? ' - ' + c.contactPhone : ''}`"
                :value="c.customerNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择部门" v-if="isDeptOptionsEnabled()" required>
          <el-select
              v-model="form.deptNo"
              filterable
              clearable
              placeholder="请选择部门（可搜索部门号/名称）"
              style="width: 100%"
              :loading="deptLoading"
          >
            <el-option
                v-for="d in deptOptions"
                :key="d.deptNo"
                :label="`${d.deptName} (${d.deptNo})`"
                :value="d.deptNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类型" required>
          <el-select
              v-model="form.serviceType"
              filterable
              clearable
              placeholder="请选择服务类型"
              style="width: 100%"
          >
            <el-option v-for="t in serviceTypeOptions" :key="t" :label="t" :value="t"/>
          </el-select>
        </el-form-item>
        <el-form-item label="服务内容" required>
          <el-select
              v-model="form.serviceContent"
              filterable
              clearable
              allow-create
              default-first-option
              placeholder="请选择或输入服务内容"
              style="width: 100%"
          >
            <el-option v-for="s in serviceContentOptions" :key="s" :label="s" :value="s"/>
          </el-select>
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="form.orderAmount" :min="0" :precision="2"/>
        </el-form-item>
        <el-form-item label="期望服务时间" required>
          <el-date-picker
              v-model="form.expectedTime"
              type="datetime"
              placeholder="选择时间"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="服务地址">
          <el-input v-model="form.serviceAddress"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remarks" type="textarea" :rows="2"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 【编辑订单】弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑订单" width="520px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="订单编号">
          <el-input v-model="editForm.orderNo" disabled/>
        </el-form-item>
        <el-form-item label="服务类型" required>
          <el-input v-model="editForm.serviceType"/>
        </el-form-item>
        <el-form-item label="服务内容" required>
          <el-input v-model="editForm.serviceContent"/>
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="editForm.orderAmount" :min="0" :precision="2"/>
        </el-form-item>
        <el-form-item label="期望服务时间">
          <el-date-picker v-model="editForm.expectedTime" type="datetime" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="服务地址">
          <el-input v-model="editForm.serviceAddress"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remarks" type="textarea" :rows="2"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 【审核订单】弹窗 -->
    <el-dialog v-model="auditDialogVisible" title="订单审核" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单编号">{{ auditForm.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务内容">{{ auditForm.serviceContent }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ auditForm.orderAmount }}</el-descriptions-item>
      </el-descriptions>
      <el-divider/>
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :label="2">通过</el-radio>
            <el-radio :label="5">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.remarks" type="textarea" :rows="3" placeholder="请填写审核备注"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAudit" :loading="auditLoading">
          提交审核
        </el-button>
      </template>
    </el-dialog>

    <!-- 【订单分派】弹窗 -->
    <el-dialog v-model="scheduleDialogVisible" title="订单分派" width="760px">
      <el-alert
          type="info"
          show-icon
          :closable="false"
          title="系统会根据技能匹配与空闲状态推荐人员；确认分派后订单进入「待接单」，待服务人员确认后进入「服务中」。"
          style="margin-bottom: 12px"
      />
      <el-table
          :data="recommendList"
          border
          style="width: 100%"
          v-loading="scheduleLoading"
          :empty-text="scheduleLoading ? '加载中…' : '暂无推荐'"
      >
        <el-table-column label="选择" width="70">
          <template #default="{ row }">
            <el-radio v-model="scheduleForm.employeeNo" :label="row.employeeNo"/>
          </template>
        </el-table-column>
        <el-table-column prop="employeeNo" label="员工编号" min-width="180" show-overflow-tooltip/>
        <el-table-column prop="employeeName" label="姓名" min-width="120" show-overflow-tooltip/>
        <el-table-column prop="workStatus" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.workStatus === 1 ? 'success' : row.workStatus === 2 ? 'warning' : 'info'">
              {{ row.workStatus === 1 ? '空闲' : row.workStatus === 2 ? '忙碌' : '休假' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="skills" label="技能" min-width="220" show-overflow-tooltip/>
        <el-table-column prop="score" label="评分" width="90"/>
        <el-table-column prop="reason" label="推荐理由" min-width="240" show-overflow-tooltip/>
      </el-table>

      <div style="margin-top: 12px">
        <el-input v-model="scheduleForm.remarks" type="textarea" :rows="2" placeholder="备注（可选）"/>
      </div>

      <template #footer>
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="scheduleLoading" @click="handleAssign">确认分派</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '../utils/request'
import {getUser} from '../utils/user.js'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ref, reactive, onMounted, watch, computed} from 'vue'

const toLocalDateTimeString = (val) => {
  if (!val) return null
  const d = val instanceof Date ? val : new Date(val)
  if (Number.isNaN(d.getTime())) return null
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const loading = ref(false)
const tableData = ref([])

const keyword = ref('')
const statusFilter = ref(null)
const page = ref(1)
const pageSize = ref(10)

const filteredData = computed(() => {
  const raw = tableData.value || []
  const k = (keyword.value || '').trim().toLowerCase()
  const status = statusFilter.value
  return raw.filter(r => {
    if (status != null && r?.orderStatus !== status) return false
    if (!k) return true
    const s = [
      r?.orderNo,
      r?.customerNo,
      r?.serviceType,
      r?.serviceContent
    ].filter(Boolean).join(' ').toLowerCase()
    return s.includes(k)
  })
})

const pagedData = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

watch([keyword, statusFilter, pageSize], () => {
  page.value = 1
})

const resetQuery = () => {
  keyword.value = ''
  statusFilter.value = null
  page.value = 1
  pageSize.value = 10
}

const showPendingSchedule = () => {
  statusFilter.value = 7
  page.value = 1
}

const auditDialogVisible = ref(false)
const scheduleDialogVisible = ref(false)

const dialogVisible = ref(false)
const dialogMode = ref('create') // create | edit
const saving = ref(false)

//【新增】和【编辑】共用一个表单
const form = reactive({
  orderNo: '',
  customerNo: '',
  serviceType: '',
  serviceContent: '',
  orderAmount: 0,
  expectedTime: null,
  serviceAddress: '',
  remarks: ''
})

const customerOptions = ref([])
const customerLoading = ref(false)

const deptOptions = ref([])
const deptLoading = ref(false)

const serviceTypeOptions = [
  '家政',
  '保洁',
  '设备维修',
  '安装',
  '搬家',
  '咨询'
]

const serviceContentPresets = {
  '家政': ['做饭', '照护老人', '照护小孩', '收纳整理'],
  '保洁': ['日常保洁', '深度保洁', '开荒保洁', '玻璃清洗'],
  '设备维修': ['空调维修', '热水器维修', '洗衣机维修', '冰箱维修', '电脑维修'],
  '安装': ['空调安装', '热水器安装', '灯具安装', '门锁安装'],
  '搬家': ['同城搬家', '小件搬运', '家具拆装'],
  '咨询': ['上门评估', '方案咨询', '费用咨询']
}

const serviceContentOptions = computed(() => {
  const t = form.serviceType
  return t && serviceContentPresets[t] ? serviceContentPresets[t] : []
})

//订单审核表单
const auditForm = reactive({
  orderNo: '',
  serviceContent: '',
  orderAmount: 0,
  auditStatus: 2,
  remarks: ''
})

//订单调度表单
const scheduleForm = reactive({
  orderNo: '',
  employeeNo: '',
  remarks: ''
})
const recommendList = ref([])

const auditLoading = ref(false)
const scheduleLoading = ref(false)

const getStatusType = (status) => {
  const map = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger',
    6: 'danger',
    7: 'warning',
    8: 'warning'
  }
  return map[status]
}

const getStatusText = status => {
  const map = {1: '待审核', 2: '已生效', 3: '服务中', 4: '已完成', 5: '已取消', 6: '异常', 7: '待分派', 8: '待接单'}
  return map[status]
}

//获取订单列表
const getList = async () => {
  loading.value = true
  try {
    tableData.value = await request.get('/api/order/')
  } catch (e) {
    console.error(e)
    ElMessage.error('获取订单列表失败，' + (e.response?.data?.message || e))
  } finally {
    loading.value = false
  }
}

const user = getUser()

//只有管理员、企业负责人能手动为指定部门创建订单
//部门经理、业务人员只能为本部门创建订单
const isDeptOptionsEnabled = () => {
  return user?.roles.some(r => ['ADMIN', 'OWNER'].includes(r))
}

const loadOptions = async () => {
  if (!customerOptions.value.length) {
    customerLoading.value = true
    try {
      customerOptions.value = await request.get('/api/customer/') || []
    } catch (e) {
      console.error(e)
      ElMessage.error('获取客户列表失败，' + (e.response?.data?.message || e))
    } finally {
      customerLoading.value = false
    }
  }

  if (isDeptOptionsEnabled() && !deptOptions.value.length) {
    deptLoading.value = true
    try {
      deptOptions.value = await request.get('/api/dept/') || []
    } catch (e) {
      console.error(e)
      ElMessage.error('获取部门列表失败，' + (e.response?.data?.message || e))
    } finally {
      deptLoading.value = false
    }
  }
}

//=====【新增订单】=====
const openCreateDialog = async () => {
  dialogMode.value = 'create'
  Object.assign(form, {
    customerNo: '',
    deptNo: isDeptOptionsEnabled() ? '' : (user?.dept?.deptNo || ''),
    serviceType: '',
    serviceContent: '',
    orderAmount: 0,
    expectedTime: null,
    serviceAddress: ''
  })
  await loadOptions()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.customerNo) {
    ElMessage.warning('请选择客户')
    return
  }
  if (isDeptOptionsEnabled() && !form.deptNo) {
    ElMessage.warning('请选择部门')
    return
  }
  if (!form.serviceType) {
    ElMessage.warning('请选择服务类型')
    return
  }
  if (!form.serviceContent) {
    ElMessage.warning('请选择或填写服务内容')
    return
  }

  saving.value = true
  try {
    const payload = {
      orderNo: form.orderNo,
      customerNo: form.customerNo,
      deptNo: form.deptNo,
      serviceType: form.serviceType,
      serviceContent: form.serviceContent,
      orderAmount: form.orderAmount,
      expectedTime: toLocalDateTimeString(form.expectedTime),
      serviceAddress: form.serviceAddress,
      remarks: form.remarks
    }

    if (dialogMode.value === 'create') {
      await request.post('/api/order/', payload);
      ElMessage.success('创建订单成功，等待审核(⌐■_■)')
      dialogVisible.value = false
      await getList()
      return
    }

    await request.put('/api/order/', payload);
    ElMessage.success('更新成功')
    dialogVisible.value = false
    await getList()
  } catch (e) {
    console.error(e)
    ElMessage.error('创建失败：' + (e.response?.data?.message || e))
  } finally {
    saving.value = false
  }
}

//=====【订单审核】=====
const openAuditDialog = (row) => {
  Object.assign(auditForm, {
    orderNo: row.orderNo,
    serviceContent: row.serviceContent,
    orderAmount: row.orderAmount,
    auditStatus: 2,
    remarks: ''
  })
  auditDialogVisible.value = true
}

const handleAudit = async () => {
  auditLoading.value = true
  try {
    await request.put(`/api/order/audit/${auditForm.orderNo}`, null, {
      params: {
        status: auditForm.auditStatus,
        remarks: auditForm.remarks
      }
    })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    getList()
  } catch (e) {
    console.error(e)
    ElMessage.error('订单审核失败：' + (e.response?.data?.message || e.message))
  } finally {
    auditLoading.value = false
  }
}

//=====【订单编辑】=====
const openEditDialog = async (row) => {
  dialogMode.value = 'edit'
  Object.assign(form, {
    orderNo: row.orderNo,
    customerNo: row.customerNo,
    deptNo: row.deptNo,
    serviceType: row.serviceType,
    serviceContent: row.serviceContent,
    orderAmount: row.orderAmount,
    expectedTime: row.expectedTime ?? null,
    serviceAddress: row.serviceAddress,
    remarks: row.remarks
  })
  await loadOptions()
  dialogVisible.value = true
}

//=====【订单调度】=====
const openScheduleDialog = async (row) => {
  scheduleForm.orderNo = row.orderNo
  scheduleForm.employeeNo = ''
  scheduleForm.remarks = ''
  recommendList.value = []

  scheduleDialogVisible.value = true
  scheduleLoading.value = true
  try {
    recommendList.value = await request.get(`/api/order/schedule/recommend/${row.orderNo}`) || []
    if (recommendList.value.length > 0) {
      scheduleForm.employeeNo = recommendList.value[0].employeeNo
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('获取推荐人员失败：' + (e.response?.data?.message || e.message))
  } finally {
    scheduleLoading.value = false
  }
}

const handleAssign = async () => {
  if (!scheduleForm.employeeNo) {
    ElMessage.warning('请选择服务人员')
    return
  }
  scheduleLoading.value = true
  try {
    const res = await request.post('/api/order/schedule/', {
      orderNo: scheduleForm.orderNo,
      employeeNo: scheduleForm.employeeNo,
      remarks: scheduleForm.remarks
    })
    const scheduleNo = res?.scheduleNo
    ElMessage.success(scheduleNo ? `分派成功，调度单号：${scheduleNo}` : '分派成功')
    await getList()
    scheduleDialogVisible.value = false
  } catch (e) {
    console.error(e)
    ElMessage.error('调度失败：' + (e.response?.data?.message || e.message))
  } finally {
    scheduleLoading.value = false
  }
}

const handleAccept = async (row) => {
  const orderNo = row?.orderNo
  if (!orderNo) return
  try {
    const res = await request.put(`/api/order/schedule/accept/${orderNo}`);
    const scheduleNo = res?.scheduleNo
    if (!scheduleNo) {
      ElMessage.error('未找到调度单，无法接单')
      return
    }

    await request.post(`/api/order/schedule/accept/${scheduleNo}`);
    ElMessage.success('接单成功，订单进入「服务中」')
    await getList()

  } catch (e) {
    console.error(e)
    ElMessage.error('接单失败：' + (e.response?.data?.message || e.message))
  }
}

const handleComplete = async (row) => {
  const orderNo = row?.orderNo
  if (!orderNo) return
  try {
    const res = await request.get(`/api/order/schedule/${orderNo}`);
    const scheduleNo = res?.scheduleNo
    if (!scheduleNo) {
      ElMessage.error('未找到调度单，无法完成')
      return
    }
    await request.put(`/api/order/schedule/complete/${scheduleNo}`);
    ElMessage.success('已完成，订单进入「已完成」，员工回到空闲状态')
    await getList()

  } catch (e) {
    console.error(e)
    ElMessage.error('订单无法完成：' + (e.response?.data?.message || e.message))
  }
}

//删除订单
const handleDelete = async (orderNo) => {
  try {
    await ElMessageBox.confirm(
        '确定要删除该订单吗？∑( 口 ||删除后无法恢复！！',
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          confirmButtonClass: 'el-button--danger'
        }
    )
    await request.delete(`/api/order/${orderNo}`)
    ElMessage.success('删除成功')
    getList()

  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error('订单删除失败, ' + (e.response?.data?.message || e.message))
    }
  }
}

//查看详情
const viewDetail = async (row) => {
  ElMessage.info('该功能尚在开发中，敬请期待...')
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.order-container {
  padding: 0;
  display: flex;
  flex: 1;
}

.order-container :deep(.el-card) {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.order-container :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  gap: 10px;
  align-items: center;
}

.pager-meta {
  color: var(--sms-text-2);
  font-size: 12px;
}
</style>
