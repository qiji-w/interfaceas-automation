import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import * as echarts from 'echarts'

import '@/assets/css/icon.css'

Vue.config.productionTip = false


Vue.use(ElementUI, { size: 'small' })
Vue.prototype.$echarts = echarts


Vue.filter('percent', function (data) {
  var result = Math.round(data * 10000);
  return result / 100 + "%"
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
