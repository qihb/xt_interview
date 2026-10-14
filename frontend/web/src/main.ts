import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'virtual:uno.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import DepartmentSelect from './components/DepartmentSelect.vue'
import AppPagination from './components/AppPagination.vue'
import router from './router'
import './style.css'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.component('DepartmentSelect', DepartmentSelect)
app.component('AppPagination', AppPagination)

app.mount('#app')
