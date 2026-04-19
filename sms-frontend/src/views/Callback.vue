<template>
  <div style="text-align: center; background-color: #fff; padding: 50px; font-size: 20px;">
    <h2>登录中...</h2>
    <p v-if="status">{{ status }}</p>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const status = ref('正在获取授权码...')

onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search)
  const code = urlParams.get('code')
  const error = urlParams.get('error')
  const verifier = localStorage.getItem('code_verifier')

  if (error) {
    status.value = '授权失败：' + error
    console.error('授权失败', error)
    setTimeout(() => {
      localStorage.clear()
      router.push('/login')
    }, 1000)
    return
  }

  if (!code || !verifier) {
    status.value = '缺少必要参数，正在返回登录页...'
    console.error('缺少参数', {code, verifier})
    setTimeout(() => {
      localStorage.clear()
      router.push('/login')
    }, 1000)
    return
  }

  try {
    status.value = '正在换取token...'

    let res = await request.post('/api/oauth2/token', {
      'grant_type': 'authorization_code',
      'code': code,
      'redirect_uri': 'http://localhost/callback',
      'client_id': 'sms-client',
      'code_verifier': verifier
    }, {
      headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    })

    if (typeof(res) === 'string') {
      res = JSON.parse(res)
    }

    console.log(res)

    status.value = '登录成功，正在跳转...'

    localStorage.setItem('access_token', res.access_token)
    localStorage.setItem('refresh_token', res.refresh_token)
    localStorage.setItem('id_token', res.id_token)
    localStorage.removeItem('code_verifier')

    setTimeout(() => {
      console.log('成功换到token，跳转首页中')
      ElMessage.success('登录成功')
      location.reload()
    }, 1000)

  } catch (e) {
    status.value = '换取token失败：' + (e.response?.data?.error_description || e.message)
    console.log('换token失败', error)
    console.log(error.response?.data)
    ElMessage.error(error.message)

    setTimeout(() => {
      localStorage.clear()
      router.push('/login')
    }, 1000)
  }
})
</script>