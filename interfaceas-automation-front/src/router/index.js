import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout'
import * as cookies from '@/util/cookies'

Vue.use(VueRouter)

const routes = [

  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: {
      title: '注册'
    }
  },
  {
    path: '/',
    name: 'Home',
    redirect: '/allProject'
  },
  {
    path: '/allproject',
    name: 'AllProject',
    component: () => import('@/views/AllProject.vue'),
    meta: {
      title: '所有项目',
      requireAuth: true
    }
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: 'project',
        name: 'Project',
        component: () => import('@/views/Project.vue'),
        meta: {
          title: '项目首页',
          requireAuth: true
        }
      },
      {
        path: 'testCase',
        name: 'TestCase',
        component: () => import('@/views/TestCase.vue'),
        meta: {
          title: '用例管理',
          requireAuth: true
        }
      },
      {
        path: 'interface',
        name: 'Interface',
        component: () => import('@/views/Interface.vue'),
        meta: {
          title: '接口管理',
          requireAuth: true
        }
      },
      {
        path: 'task',
        name: 'Task',
        component: () => import('@/views/Task.vue'),
        meta: {
          title: '任务管理',
          requireAuth: true
        }
      },
      {
        path: 'testRecord',
        name: 'TestRecord',
        component: () => import('@/views/TestRecord.vue'),
        meta: {
          title: '运行记录',
          requireAuth: true
        }
      },
      {
        path: 'testReport',
        name: 'TestReport',
        component: () => import('@/views/TestReport.vue'),
        meta: {
          title: '测试报告',
          requireAuth: true
        }
      },
      {
        path: 'dataAnalysis/:id?',
        name: 'DataAnalysis',
        component: () => import('@/views/DataAnalysis.vue'),
        meta: {
          title: '数据分析',
          requireAuth: true
        }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: {
          title: '用户管理',
          requireAuth: true
        }
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requireAuth)) {
    let user = cookies.getCurrentUser() //从cookies获取当前登录用户
    if (user !== null && user !== undefined && user.username) {
      next()
    } else {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  } else {
    next() 
  }
})

export default router
