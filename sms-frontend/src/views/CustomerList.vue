<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户列表</span>
        </div>
      </template>

      <div class="table-toolbar">
        <el-input
            v-model="keyword"
            placeholder="搜索：编号/姓名/电话/地址"
            clearable
            style="max-width: 320px"
        />
        <el-select v-model="statusFilter" clearable placeholder="跟进状态" style="width: 140px">
          <el-option :value="1" label="待跟进"/>
          <el-option :value="2" label="已跟进"/>
          <el-option :value="3" label="无需跟进"/>
        </el-select>
        <el-button type="primary" @click="openCreate">新增客户</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table
          :data="pagedData"
          border
          style="width: 100%"
          v-loading="loading"
          :empty-text="loading ? '加载中…' : '暂无数据'"
      >
        <el-table-column prop="customerNo" label="客户编号" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="customerType" label="客户类型" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.customerType === 1 ? '个人' : '企业' }}
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户姓名" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="contactPerson" label="联系人姓名" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="contactPhone" label="联系电话" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip/>
        <el-table-column prop="followUpStatus" label="状态" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.followUpStatus)">
              {{ getStatusText(row.followUpStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.customerNo)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增客户' : '编辑客户'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="客户编号" v-if="dialogMode === 'edit'">
          <el-input v-model="form.customerNo" disabled/>
        </el-form-item>
        <el-form-item label="客户姓名">
          <el-input v-model="form.customerName"/>
        </el-form-item>
        <el-form-item label="客户类型">
          <el-select v-model="form.customerType">
            <el-option label="企业" :value="0"></el-option>
            <el-option label="个人" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="联系人姓名">
          <el-input v-model="form.contactPerson"/>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone"/>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address"/>
        </el-form-item>
        <el-form-item label="跟进状态" v-if="dialogMode === 'edit'">
          <el-select v-model="form.followUpStatus">
            <el-option label="待跟进" :value="1"></el-option>
            <el-option label="已跟进" :value="2"></el-option>
            <el-option label="无需跟进" :value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="saving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '../utils/request'
import {ref, onMounted, computed, watch} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('create') // create | edit

const tableData = ref([])

const keyword = ref('')
const statusFilter = ref(null)
const page = ref(1)
const pageSize = ref(10)

const filteredData = computed(() => {
  const raw = tableData.value || []
  const k = (keyword.value || '').trim().toLowerCase()
  const status = statusFilter.value
  if (!k) return raw
  return raw.filter(r => {
    if (status != null && r?.followUpStatus !== status) return false
    const s = [
      r?.customerNo,
      r?.customerName,
      r?.contactPhone,
      r?.address
    ].filter(Boolean).join(' ').toLowerCase()
    return s.includes(k)
  })
})

const pagedData = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

watch([keyword, pageSize], () => {
  page.value = 1
})

const resetQuery = () => {
  keyword.value = ''
  statusFilter.value = null
  page.value = 1
  pageSize.value = 10
}

//表单
const form = ref({
  customerNo: '',
  customerName: '',
  customerType: 0,
  contactPerson: '',
  contactPhone: '',
  address: '',
  followUpStatus: 1
})

const getStatusType = status => {
  const map = {1: 'warning', 2: 'success', 3: 'info'}
  return map[status]
}

const getStatusText = status => {
  const map = {1: '待跟进', 2: '已跟进', 3: '无需跟进'}
  return map[status]
}

//获取客户列表
const getList = async () => {
  loading.value = true
  try {
    tableData.value = await request.get('/api/customer/')
  } catch (e) {
    console.error(e)
    ElMessage.error('获取客户列表失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

//打开弹窗
const openCreate = () => {
  dialogMode.value = 'create'
  form.value = {
    customerName: '',
    customerType: 0,
    contactPerson: '',
    contactPhone: '',
    address: '',
  }
  dialogVisible.value = true
}

const openEdit = (row) => {
  dialogMode.value = 'edit'
  form.value = {
    customerNo: row.customerNo,
    customerName: row.customerName,
    customerType: row.customerType,
    contactPerson: row.contactPerson,
    contactPhone: row.contactPhone,
    address: row.address,
    followUpStatus: row.followUpStatus
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  saving.value = true
  try {
    if (dialogMode.value === 'create') {
      await request.post('/api/customer/', form.value);
      ElMessage.success('创建成功！')
      dialogVisible.value = false
      await getList()
      return
    }

    await request.put(`/api/customer/${form.value.customerNo}`, form.value);
    ElMessage.success('更新成功！')
    await getList()
    dialogVisible.value = false

  } catch (e) {
    console.error(e)
    ElMessage.error((dialogMode.value === 'create' ? '创建失败：' : '更新失败：') + (e.response?.data?.message || e))
  } finally {
    saving.value = false
  }
}

//删除客户
const handleDelete = async (no) => {
  try {
    await ElMessageBox.confirm('确定要删除吗！？∑( 口 ||', '提示', {type: 'warning'})
    await request.delete(`/api/customer/${no}`);
    ElMessage.success('删除成功！')
    getList()

  } catch (e) {
    console.error(e)
    ElMessage.error('删除失败：' + (e.response?.data?.message || e))
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
