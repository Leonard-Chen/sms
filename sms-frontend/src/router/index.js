import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import {ElMessage} from 'element-plus'
import {getUser} from '../utils/user.js'
import Callback from '../views/Callback.vue'
import DeptList from '../views/DeptList.vue'
import UserList from '../views/UserList.vue'
import OrderList from '../views/OrderList.vue'
import Dashboard from '../views/Dashboard.vue'
import CustomerList from '../views/CustomerList.vue'
import EmployeeList from '../views/EmployeeList.vue'
import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {path: '/', redirect: '/home'},
    {path: '/home', component: Home},
    {path: '/login', component: Login},
    {path: '/callback', component: Callback},
    {path: '/order', component: OrderList, meta: {requiresAuth: true, roles: ['OWNER', 'MANAGER', 'ADMIN', 'USER']}},
    {
        path: '/customer',
        component: CustomerList,
        meta: {requiresAuth: true, roles: ['OWNER', 'MANAGER', 'ADMIN', 'USER']}
    },
    {path: '/employee', component: EmployeeList, meta: {requiresAuth: true, roles: ['OWNER', 'MANAGER', 'ADMIN']}},
    {path: '/dept', component: DeptList, meta: {requiresAuth: true, roles: ['OWNER', 'ADMIN']}},
    {path: '/dashboard', component: Dashboard, meta: {requiresAuth: true, roles: ['OWNER', 'MANAGER', 'ADMIN']}},
    {path: '/user', component: UserList, meta: {requiresAuth: true, roles: ['ADMIN']}},
]

//动态路由，根据权限加载
const asyncRoutes = [
    {
        path: '/home',
        component: () => import('../views/Home.vue'),
        meta: {title: '主页'}
    },
    {
        path: '/dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: {title: '数据看板', roles: ['OWNER', 'MANAGER', 'ADMIN']}
    },
    {
        path: '/customer',
        component: () => import('../views/CustomerList.vue'),
        meta: {title: '客户管理', roles: ['OWNER', 'MANAGER', 'ADMIN', 'USER']}
    },
    {
        path: '/order',
        component: () => import('../views/OrderList.vue'),
        meta: {title: '订单管理', roles: ['OWNER', 'MANAGER', 'ADMIN', 'USER']}
    },
    {
        path: '/employee',
        component: () => import('../views/EmployeeList.vue'),
        meta: {title: '员工管理', roles: ['OWNER', 'MANAGER', 'ADMIN']}
    },
    {
        path: '/dept',
        component: () => import('../views/DeptList.vue'),
        meta: {title: '部门管理', roles: ['OWNER', 'ADMIN']}
    },
    {
        path: '/user',
        component: () => import('../views/UserList.vue'),
        meta: {title: '用户管理', roles: ['ADMIN']}
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('access_token')

    //访问登录页，有token直接跳首页
    if (to.path === '/login' || to.path === '/callback') {
        token ? next('/home') : next()
        return
    }

    //访问其他页面，没token就跳登录页
    if (to.meta?.requiresAuth && !token) {
        ElMessage.error('请先登录!')
        next('/login')
        return
    }

    const user = getUser()

    const hasRoutes = router.getRoutes().map(r => r.path)
    asyncRoutes.forEach(route => {
        if (!hasRoutes.includes(route.path)) {
            //校验角色权限，有权限才添加路由
            if (route.meta?.roles?.some(r => user?.roles?.includes?.(r))) {
                router.addRoute(route)
            }
        }
    })

    if (to.meta?.roles && !to.meta?.roles.some(r => user?.roles?.includes?.(r))) {
        ElMessage.error('你没有权限访问该页面(；′⌒`)')
        next(from.path)
        return
    }

    next()
})

export default router
