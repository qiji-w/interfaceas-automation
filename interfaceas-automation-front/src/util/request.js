import axios from 'axios'
import { Message, Loading } from 'element-ui'
import router from '@/router'
import * as cookies from '@/util/cookies'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 10000
})

let loading  = null

service.defaults.withCredentials = true;

service.interceptors.request.use(
  config => {
    loading = Loading.service({
      lock: true,
      text: '数据加载中...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    })

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    if(loading!=null){
      loading.close()
    }
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      cookies.removeCurrentUser()
      if(loading!=null){
        loading.close()
      }
      router.push('/login')
      return
    }
    if (error.response && error.response.status === 996) {
      Message({
        message: error.response.data.message,
        type: 'warning',
        duration: 5 * 1000
      })
    } else {
      Message({
        message: '返回处理异常。' + error,
        type: 'error',
        duration: 5 * 1000
      })
    }
    if(loading!=null){
      loading.close()
    }
    return Promise.reject(error)
  }
)

export default service
