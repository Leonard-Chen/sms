<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>员工列表</span>
      </div>
    </template>

    <div class="table-toolbar">
      <el-input
          v-model="keyword"
          placeholder="搜索：编号/姓名/部门/电话/职位"
          clearable
          style="max-width: 340px"
      />
      <el-select v-model="statusFilter" clearable placeholder="状态" style="width: 140px">
        <el-option :value="1" label="空闲"/>
        <el-option :value="2" label="忙碌"/>
        <el-option :value="3" label="休假"/>
      </el-select>
      <el-button type="primary" @click="openCreate">新增员工</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <el-table
        :data="pagedData"
        border
        style="width: 100%"
        v-loading="loading"
        :empty-text="loading ? '加载中…' : '暂无数据'"
    >
      <el-table-column prop="employeeNo" label="员工编号" min-width="200" show-overflow-tooltip/>
      <el-table-column prop="employeeName" label="姓名" min-width="120" show-overflow-tooltip/>
      <el-table-column prop="deptName" label="部门" min-width="140" show-overflow-tooltip/>
      <el-table-column prop="position" label="职位" min-width="140" show-overflow-tooltip/>
      <el-table-column prop="phone" label="电话" min-width="150" show-overflow-tooltip/>
      <el-table-column prop="workStatus" label="状态" min-width="110">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.workStatus)">
            {{ getStatusText(row.workStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hiredDate" label="入职日期" min-width="130" show-overflow-tooltip/>
      <el-table-column prop="createTime" label="创建时间" min-width="170" show-overflow-tooltip/>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.employeeNo)">删除</el-button>
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

  <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增员工' : '编辑员工'" width="620px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="员工编号" v-if="dialogVisible === 'edit'">
        <el-input v-model="form.employeeNo" disabled/>
      </el-form-item>
      <el-form-item label="姓名" required>
        <el-input v-model="form.employeeName" placeholder="请输入员工姓名"/>
      </el-form-item>
      <el-form-item label="部门" required>
        <el-select v-model="form.deptNo" style="width: 100%" filterable placeholder="请选择部门">
          <el-option
              v-for="d in deptOptions"
              :key="d.deptNo"
              :label="`${d.deptName} (${d.deptNo})`"
              :value="d.deptNo"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="职位">
        <el-input v-model="form.position"/>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.phone"/>
      </el-form-item>
      <el-form-item label="技能">
        <el-input v-model="form.skills" placeholder="多个用逗号分隔"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.workStatus" style="width: 100%">
          <el-option :value="1" label="空闲"/>
          <el-option :value="2" label="忙碌"/>
          <el-option :value="3" label="休假"/>
        </el-select>
      </el-form-item>
      <el-form-item label="入职日期">
        <el-date-picker v-model="form.hiredDate" type="date" style="width: 100%" placeholder="选择日期"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import request from '../utils/request'
import {computed, onMounted, ref, watch} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const deptOptions = ref([])

const keyword = ref('')
const statusFilter = ref(null)
const page = ref(1)
const pageSize = ref(10)

const filteredData = computed(() => {
  const raw = tableData.value || []
  const k = (keyword.value || '').trim().toLowerCase()
  const status = statusFilter.value
  return raw.filter(r => {
    if (status != null && r?.workStatus !== status) return false
    if (!k) return true
    const s = [
      r?.employeeNo,
      r?.employeeName,
      r?.deptName,
      r?.phone,
      r?.position
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

const dialogVisible = ref(false)
const dialogMode = ref('create') // create | edit
const saving = ref(false)
const form = ref({
  employeeNo: '',
  employeeName: '',
  deptNo: '',
  position: '',
  phone: '',
  skills: '',
  workStatus: 1,
  hiredDate: null
})

const getStatusType = status => {
  const map = {1: 'success', 2: 'warning', 3: 'info'}
  return map[status]
}

const getStatusText = status => {
  const map = {1: '空闲', 2: '忙碌', 3: '休假'}
  return map[status]
}

const getDeptOptions = async () => {
  try {
    deptOptions.value = await request.get('/api/dept/')
  } catch (e) {
    console.error(e)
    ElMessage.error('获取部门列表失败，' + (e.response?.data?.message || e))
  }
}

const getList = async () => {
  loading.value = true
  try {
    tableData.value = await request.get('/api/employee/') || []
    await getDeptOptions()
    tableData.value.forEach(row => row.deptName = deptOptions.value.filter(dept => dept.deptNo === row.deptNo)[0].deptName)
  } catch (e) {
    console.error(e)
    ElMessage.error('获取员工列表失败，' + (e.response?.data?.message || e))
  } finally {
    loading.value = false
  }
}

const openCreate = async () => {
  dialogMode.value = 'create'
  form.value = {
    employeeNo: '',
    employeeName: '',
    deptNo: null,
    position: '',
    phone: '',
    skills: '',
    workStatus: 1,
    hiredDate: null
  }
  await getDeptOptions()
  dialogVisible.value = true
}

const openEdit = async (row) => {
  dialogMode.value = 'edit'
  form.value = {
    employeeNo: row.employeeNo,
    employeeName: row.employeeName,
    deptNo: row.deptNo ?? null,
    position: row.position,
    phone: row.phone,
    skills: row.skills,
    workStatus: row.workStatus ?? 1,
    hiredDate: row.hiredDate ?? null
  }
  await getDeptOptions()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.employeeName) {
    ElMessage.warning('请填写姓名')
    return
  }
  if (!form.value.deptNo) {
    ElMessage.warning('请选择部门')
    return
  }

  saving.value = true
  try {
    const payload = {
      employeeNo: form.value.employeeNo,
      employeeName: form.value.employeeName,
      deptNo: form.value.deptNo,
      position: form.value.position,
      phone: form.value.phone,
      skills: form.value.skills,
      workStatus: form.value.workStatus,
      hiredDate: form.value.hiredDate
    }

    if (dialogMode.value === 'create') {
      await request.post('/api/employee/', payload)
      ElMessage.success('创建成功')
      dialogVisible.value = false
      await getList()
      return
    }
    await request.put('/api/employee/', payload)
    ElMessage.success('更新成功')
    dialogVisible.value = false
    await getList()

  } catch (e) {
    console.error(e)
    ElMessage.error(e.response?.data?.message || e)
  } finally {
    saving.value = false
  }
}

const handleDelete = async (employeeNo) => {
  try {
    await ElMessageBox.confirm('确定删除该员工吗？∑( 口 ||', '提示', {type: 'warning'})
    await request.delete(`/api/employee/${employeeNo}`)
    ElMessage.success('删除成功')
    await getList()

  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error('删除失败，' + (e.response?.data?.message || e))
    }
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
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

