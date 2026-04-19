import axios from 'axios'
import {ElMessage} from "element-plus";

const request = axios.create({
    baseURL: 'http://localhost',
    timeout: 10000, //10秒超时
    withCredentials: true
})

request.interceptors.request.use(
    config => {
        //换token的请求不能带Authorization头
        //因为/oauth2/token接口用的是client_id和code_verifier，不是Bearer token
        if (!config.url.includes('/oauth2/token')) {
            const token = localStorage.getItem('access_token')
            if (token) {
                config.headers.Authorization = `Bearer ${token}`
            }
        }
        return config
    },
    error => {
        console.error('请求出错：' + error)
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => response.data,
    error => {
        console.error('响应出错：' + error)
        //换token请求失败时不要跳登录页，业务接口401才跳
        if (error.response && error.response.status === 401 && !error.config.url.includes('/oauth2/token')) {
            ElMessage.error('登录已过期，请重新登录')
            localStorage.clear()
            window.location.href = '/login'
        }
        if (error.message.includes('timeout')) {
            ElMessage.error('服务器繁忙，请稍后再试')
        }
        return Promise.reject(error)
    }
)

export default request
