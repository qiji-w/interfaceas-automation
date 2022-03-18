import Vue from 'vue'
import Vuex from 'vuex'
import * as cookies from '@/util/cookies'
import * as storage from '@/util/storage'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    //菜单展开状态
    isCollapse:false,
    //当前用户
    currentUser: cookies.getCurrentUser()||null,
    //当前项目
    currentProject: storage.getCurrentProject()||null,

    //测试任务详情数据列表
    taskDetails:[],
    //当前测试用例
    currentTestCase:{},
    //当前拷贝的用例
    copyTestCase:null,
    //快捷标签列表
    tagsList:[]
  },
  getters:{
    getCollapse: state => {
      return state.isCollapse
    },
    getCurrentUser:state=>{
      return state.currentUser
    },
    getCurrentProject:state=>{
      return state.currentProject
    },

    getCurrentTestCase:state=>{
      return state.currentTestCase
    },
    getTaskDetails:state=>{
      return state.taskDetails
    },

    getCopyTestCase:state=>{
      return state.copyTestCase
    },
    getTagsList:state=>{
      return state.tagsList
    }
  },
  mutations: {
    toggleCollapse(state) {
      state.isCollapse = ! state.isCollapse
    },
    setCurrentUser(state,user){
      state.currentUser = user
      cookies.setCurrentUser(user)
    },
    removeCurrentUser(state){
      state.currentUser = null
      cookies.removeCurrentUser()
    },
    setCurrentProject(state,project){
      state.currentProject  = project
      storage.setCurrentProject(project)
    },
    removeCurrentProject(state){
      state.currentProject  = null
      storage.removeCurrentProject()
    },
    
    setCurrentTestCase(state,currentTestCase){
      state.currentTestCase = currentTestCase
    },
    setTaskDetails(state,taskDetails){
      state.taskDetails = taskDetails
    },
    setCopyTestCase(state,copyTestCase){
      state.copyTestCase = copyTestCase
    },
    setTagsList(state,tagsList){
      state.tagsList =tagsList
    }
  },
  actions: {
  },
  modules: {
  }
})
