<template>
  <!-- 登录/回调页：不显示侧边栏与顶部栏 -->
  <div v-if="isAuthPage" style="height: 100vh">
    <router-view/>
  </div>

  <!-- 业务页面：显示完整布局 -->
  <el-container v-else class="app-shell">
    <!-- 侧边栏 -->
    <el-aside class="app-aside" width="220px">
      <div class="app-logo">
        <div class="app-logo-badge">SMS</div>
        <div class="app-logo-text">
          <div class="app-logo-title">SMS 控制台</div>
          <div class="app-logo-subtitle">Business Suite</div>
        </div>
      </div>
      <el-menu
          :default-active="activeMenu"
          router
          class="app-menu"
      >
        <el-menu-item index="/home">
          <el-icon>
            <House/>
          </el-icon>
          <span>主页</span>
        </el-menu-item>

        <el-menu-item index="/customer">
          <el-icon>
            <User/>
          </el-icon>
          <span>客户管理</span>
        </el-menu-item>

        <el-menu-item index="/order">
          <el-icon>
            <Document/>
          </el-icon>
          <span>订单管理</span>
        </el-menu-item>

        <el-menu-item index="/employee" v-if="user?.roles.some(r => ['OWNER', 'MANAGER', 'ADMIN'].includes(r))">
          <el-icon>
            <Avatar/>
          </el-icon>
          <span>员工管理</span>
        </el-menu-item>

        <el-menu-item index="/dept" v-if="user?.roles.some(r => ['OWNER', 'ADMIN'].includes(r))">
          <el-icon>
            <OfficeBuilding/>
          </el-icon>
          <span>部门管理</span>
        </el-menu-item>

        <el-menu-item index="/dashboard" v-if="user?.roles.some(r => ['OWNER', 'MANAGER', 'ADMIN'].includes(r))">
          <el-icon>
            <DataBoard/>
          </el-icon>
          <span>数据看板</span>
        </el-menu-item>

        <el-menu-item index="/user" v-if="user?.roles.includes('ADMIN')">
          <el-icon>
            <UserFilled/>
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 页头 -->
      <el-header class="app-header">
        <div class="app-header-left">
          <div class="app-header-title">小型服务型企业业务管理系统</div>
        </div>

        <!-- 退出登录 -->
        <div class="app-header-right">
          <div class="app-user" v-if="token && token !== ''">
            <div class="app-user-name">{{ user?.username || '用户' }}</div>
            <el-button link type="danger" @click="handleLogout">退出</el-button>
          </div>
          <el-button v-else link type="primary" @click="goLogin">登录</el-button>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
  <AIFloatBall/>
</template>

<script setup>
import {computed} from 'vue'
import {getUser} from './utils/user.js'
import request from './utils/request.js'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import AIFloatBall from './components/ai/AIFloatBall.vue'
import {Avatar, DataBoard, Document, House, OfficeBuilding, User, UserFilled} from '@element-plus/icons-vue'

const router = useRouter()

const token = localStorage.getItem('access_token')
const user = getUser()

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
        "确定要退出系统吗！？∑( 口 ||",
        "登出确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "手滑了...",
          type: "warning"
        }
    )

    await request.post('/api/logout')

    ElMessage.success('登出成功')

    setTimeout(() => {
      localStorage.clear()
      delete request.defaults.headers.common['Authorization']
      window.location.href = '/'
    }, 1000)

  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error('登出失败：' + (e.response?.data?.message || e.message))
    }
  }
}

const goLogin = () => {
  router.push('/login')
}

const route = useRoute()
const activeMenu = computed(() => route.path)
const isAuthPage = computed(() => route.path === '/login' || route.path === '/callback')
</script>

<style>
.app-shell {
  height: 100vh;
  background: var(--sms-bg);
}

.app-aside {
  background: var(--sms-surface);
  border-right: 0 !important;
  box-shadow: inset -1px 0 0 var(--sms-border);
  padding: 14px 12px;
  box-sizing: border-box;
}

.app-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 10px 14px;
}

.app-logo-badge {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-weight: 800;
  letter-spacing: 0.6px;
  color: white;
  background: linear-gradient(135deg, var(--sms-accent), var(--sms-accent-2));
  box-shadow: 0 10px 22px rgba(37, 99, 235, 0.22);
}

.app-logo-title {
  font-weight: 800;
  font-size: 14px;
  color: var(--sms-text);
  line-height: 1.2;
}

.app-logo-subtitle {
  margin-top: 2px;
  font-size: 12px;
  color: var(--sms-text-2);
}

.app-menu {
  border-right: 0 !important;
}

.app-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--sms-surface);
  border-bottom: 1px solid var(--sms-border);
}

.app-header-title {
  font-weight: 700;
  color: var(--sms-text);
}

.app-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.app-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.app-user-name {
  color: var(--sms-text);
  font-weight: 600;
  font-size: 13px;
}

.app-main {
  padding: 0;
  min-height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  background: #fff;
  border-left: 0 !important;
}

.app-main > * {
  flex: 1;
  min-height: 100%;
  width: 100%;
  margin: 0 !important;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
  border-left: 0 !important;
}

.app-main .el-card {
  margin: 0 !important;
  border-radius: 0 !important;
  border: 0 !important;
  box-shadow: none !important;
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.app-main .el-card__header {
  padding: 12px 12px 10px !important;
  border-bottom: 1px solid var(--sms-border) !important;
}

.app-main .el-card__body {
  padding: 10px 12px 10px !important;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.app-main .table-toolbar,
.app-main .table-footer {
  padding: 0;
}

.app-header,
.app-main,
.app-main > * {
  box-shadow: none !important;
}

.app-main .el-table,
.app-main .el-table__inner-wrapper,
.app-main .el-table__header-wrapper,
.app-main .el-table__body-wrapper {
  border-left: 0 !important;
}
</style>
