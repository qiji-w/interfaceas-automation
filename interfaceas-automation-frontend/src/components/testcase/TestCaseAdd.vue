<!--测试用例新增-->
<template>
  <div class="test-case-add">
    <el-form :model="testCase"
             label-width="80px">
      <el-form-item label="接口模块">
        <el-select v-model="testCase.moduleId"
                   placeholder="接口模块"
                   style="width: 100%">
          <el-option v-for="item in modules"
                     :key="item.id"
                     :label="item.name"
                     :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="接口名称">
        <el-select v-model="testCase.interfaceId"
                   placeholder="请选择接口"
                   style="width: 100%">
          <el-option v-for="item in currentInterfaces"
                     :key="item.id"
                     :label="item.name"
                     :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用例名称">
        <el-input v-model="testCase.name"
                  autocomplete="off"
                  placeholder="请输入用例名称">
        </el-input>
      </el-form-item>
    </el-form>
    <div slot="footer"
         class="dialog-footer">
      <el-button @click="cancel"
                 size="mini">取 消</el-button>
      <el-button type="primary"
                 @click="save"
                 size="mini">确 定</el-button>
    </div>
  </div>
</template>

<script>
import * as testcaseApi from '@/api/testcase'
import * as taskApi from '@/api/task'
import * as interfaceApi from '@/api/interface'

export default {
  name: 'TestCaseAdd',
  props: {
    testSuitId: Number,
    taskId: Number,
  },
  data () {
    return {
      modules: [],
      interfaces: [],
      testCase: {},
    }
  },
  computed: {
    project () {
      return this.$store.getters.getCurrentProject
    },
    //计算当前选择模块下的所有接口
    currentInterfaces () {
      let moduleId = this.testCase.moduleId
      if (moduleId) {
        return this.interfaces.filter((item) => {
          return item.moduleId == moduleId
        })
      } else {
        return []
      }
    },
  },
  created () {
    this.getInitData()
  },
  methods: {
    async getInitData () {
      //获取所有模块
      await taskApi.getModulesByTaskId(this.taskId).then((response) => {
        let result = response.data
        this.modules = result.data
      })
      //获取所有接口
      await interfaceApi.queryByProjectId(this.project.id).then((response) => {
        let result = response.data
        this.interfaces = result.data
      })
    },
    save () {
      this.testCase.projectId = this.project.id
      this.testCase.testSuitId = this.testSuitId
      this.testCase.taskId = this.taskId
      let operation = null
      if (!this.testCase.id) {
        operation = testcaseApi.create(this.testCase)
      } else {
        operation = testcaseApi.modify(this.testCase)
      }
      operation.then((response) => {
        if (response.data.code === 0) {
          this.$message({
            message: '保存成功。',
            type: 'success',
          })
          this.$emit('callbackForSave', response.data.data.id, this.testSuitId, this.taskId)
        } else {
          this.$message({
            message: response.data.message,
            type: 'warning',
          })
        }
      })
    },
    cancel () {
      this.$emit('callbackForCancel')
    },
  },
}
</script>

<style scoped>
</style>