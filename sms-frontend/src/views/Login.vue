<template>
  <div class="login-page">
    <div class="login-shell">
      <div class="login-left">
        <div class="brand-left">
          <div class="brand-badge">SMS</div>
          <div class="brand-meta">
            <div class="brand-title">小型服务型企业业务管理系统</div>
            <div class="brand-subtitle">统一管理您的客户、订单、员工调度及业务数据</div>
          </div>
        </div>
        <div class="hero-title">让业务更高效</div>
        <div class="hero-desc">登录后即可进行订单流转、员工分派、状态追踪和看板分析等工作。</div>
      </div>

      <div class="login-right">
        <el-card class="login-card" shadow="never">
          <div class="login-title">欢迎登录</div>
          <div class="login-subtitle">请输入账号密码继续</div>

          <el-form ref="formRef" :rules="rules" :model="form" @submit.prevent="login">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" autocomplete="username"/>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码" show-password autocomplete="current-password"/>
            </el-form-item>
            <el-button class="login-btn" type="primary" @click="login">登录系统</el-button>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {pkce} from '../utils/pkce'
import {ElMessage} from 'element-plus'
import request from '../utils/request'

const form = ref({username: '', password: ''})
const formRef = ref(null)

//表单校验规则
const rules = {
  username: [
    {required: true, message: '请输入账号', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, message: '密码长度不少于6个字符', trigger: 'blur'}
  ]
}

const login = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res = await request.post('/api/auth/login', form.value, {
          headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })

        if (typeof(res) === 'string') {
          res = JSON.parse(res)
        }

        console.log(res)

        localStorage.setItem('user', JSON.stringify(res.user))

        const verifier = pkce.codeVerifier()
        const challenge = await pkce.codeChallenge(verifier)
        localStorage.setItem('code_verifier', verifier)

        const url = new URL('http://localhost:9000/oauth2/authorize')
        url.searchParams.set('response_type', 'code');
        url.searchParams.set('client_id', 'sms-client');
        url.searchParams.set('redirect_uri', 'http://localhost/callback');
        url.searchParams.set('scope', 'openid profile');
        url.searchParams.set('code_challenge', challenge);
        url.searchParams.set('code_challenge_method', 'S256');

        window.location.href = url.toString()
      } catch (e) {
        ElMessage.error('登录失败：' + (e.response?.data?.message || e.message))
      }
    } else {
      ElMessage.warning('请正确输入用户名或密码')
      return false
    }
  })
}
</script>

<style>
.login-page {
  min-height: 100vh;
  background: radial-gradient(1200px 600px at 20% -10%, rgba(37, 99, 235, 0.18), rgba(37, 99, 235, 0) 60%),
  radial-gradient(1000px 520px at 120% 110%, rgba(124, 58, 237, 0.18), rgba(124, 58, 237, 0) 60%),
  #f7f9fc;
}

.login-shell {
  min-height: 100vh;
  max-width: 1120px;
  margin: 0 auto;
  padding: 28px 22px;
  display: flex;
  gap: 24px;
  box-sizing: border-box;
}

.login-left,
.login-right {
  flex: 1;
  min-width: 0;
}

.login-left {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 12px 8px;
}

.brand-badge {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-weight: 800;
  letter-spacing: 0.8px;
  background: linear-gradient(135deg, var(--sms-accent), var(--sms-accent-2));
  color: #fff;
}

.brand-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}

.brand-subtitle {
  color: rgba(17, 24, 39, 0.6);
  font-size: 13px;
}

.hero-title {
  margin-top: 40px;
  font-size: 34px;
  line-height: 1.2;
  font-weight: 800;
  color: #0f172a;
}

.hero-desc {
  margin-top: 14px;
  max-width: 460px;
  color: #475569;
  font-size: 15px;
  line-height: 1.7;
}

.login-right {
  display: grid;
  place-items: center;
}

.login-card {
  width: min(430px, 100%);
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  background: #ffffff;
}

.login-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}

.login-subtitle {
  margin-top: 8px;
  margin-bottom: 18px;
  font-size: 14px;
  color: #64748b;
}

.login-btn {
  width: 100%;
  height: 40px;
  margin-top: 4px;
}

@media (max-width: 900px) {
  .login-shell {
    flex-direction: column;
    justify-content: center;
  }

  .login-left {
    padding-bottom: 0;
  }

  .hero-title {
    margin-top: 18px;
    font-size: 28px;
  }
}
</style>