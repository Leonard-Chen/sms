import './style.css'
import App from './App.vue'
import router from './router'//路由
import {createApp} from 'vue'
import 'element-plus/dist/index.css'//样式
import ElementPlus from 'element-plus'//ElementPlus
import role from './directive/role.js'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'//图标

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(router)
app.use(ElementPlus)
app.directive('role', role)
app.mount('#app')
