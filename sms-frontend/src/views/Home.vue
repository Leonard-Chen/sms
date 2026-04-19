<template>
  <div class="home-container">
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <h1>小型服务型企业业务管理系统</h1>
        <p class="desc">高效管理您的客户和订单，助力企业数字化运营</p>
        <p class="user-info" v-if="user">欢迎回来，{{ user.realName || '不知名的用户' }}！</p>
        <p class="user-info" v-else>您还未登录，请先<a href="/login">登录</a></p>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8" v-for="item in links" :key="item.path">
        <el-card
            class="link-card"
            @click="goto(item.path)"
            v-if="item.roles ? item.roles.some(r => user?.roles.includes(r)) : true"
        >
          <div class="link-icon">
            <el-icon :size="40">
              <component :is="item.icon"/>
            </el-icon>
          </div>
          <div class="link-text">{{ item.name }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {getUser} from '../utils/user.js'
import {Avatar, DataBoard, Document, House, OfficeBuilding, User, UserFilled} from '@element-plus/icons-vue'

const router = useRouter()

const user = getUser()

//快捷入口
const links = ref([
  {name: '主页', path: '/home', icon: House},
  {name: '客户管理', path: '/customer', icon: User},
  {name: '订单管理', path: '/order', icon: Document},
  {name: '员工管理', path: '/employee', icon: Avatar, roles: ['OWNER', 'MANAGER', 'ADMIN']},
  {name: '部门管理', path: '/dept', icon: OfficeBuilding, roles: ['OWNER', 'ADMIN']},
  {name: '数据看板', path: '/dashboard', icon: DataBoard, roles: ['OWNER', 'MANAGER', 'ADMIN']},
  {name: '用户管理', path: '/user', icon: UserFilled, roles: ['ADMIN']}
])

//跳转到对应页面
const goto = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  text-align: center;
  padding: 40px 0;
  background: linear-gradient(120deg, #f5f7fa 0%, #e4eaf5 100%);
}

.welcome-content h1 {
  color: #303133;
  margin-bottom: 16px;
  font-size: 32px;
}

.welcome-content .desc {
  color: #606266;
  font-size: 18px;
}

.link-card {
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.link-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.link-icon {
  color: #409eff;
  margin-bottom: 16px;
}
</style>
