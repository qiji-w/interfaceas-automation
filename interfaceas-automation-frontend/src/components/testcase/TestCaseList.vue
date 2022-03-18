<!--测试用例列表-->
<template>
  <div class="test-case-list">
    <div id="inter_list">
      <div style="
          height: 50px;
          font: bold 24px/50px 'microsoft yahei';
          color: #000;
          text-align: center;
        ">
        <i class="el-icon-s-management">用例管理</i>
      </div>
      <div style="background: #f0f0f0; height: 5px"></div>
      <el-menu class="el-menu-vertical-demo"
               style="height: calc(100vh - 170px); overflow-y: auto"
               :default-active="defaultActive">
        <!--测试任务-->
        <el-submenu :index="task.id + ''"
                    v-for="task in taskDetails"
                    :key="task.id">
          <template slot="title"
                    style="font-weight: bold">
            <i class="el-icon-folder-opened"></i>
            <span slot="title">{{ task.name }}</span>
          </template>
          <!--测试套件-->
          <el-submenu :index="task.id + '_' + testSuit.id"
                      v-for="testSuit in task.testSuits"
                      :key="testSuit.id">
            <template slot="title">
              <i class="el-icon-folder"></i>
              <span>{{ testSuit.name }}
                <el-tooltip content="删除测试套件"
                            placement="right"
                            effect="light">
                  <i class="el-icon-document"></i>
                  <span>
                    <el-button type="text"
                               icon="el-icon-delete"
                               @click.stop="removeTestSuit(testSuit)"
                               style="margin-bottom: 5px"></el-button>
                  </span>
                </el-tooltip>
              </span>
            </template>
            <!--测试用例-->
            <el-menu-item :index="task.id + '_' + testSuit.id + '_' + testCase.id"
                          v-for="testCase in testSuit.testCases.slice().sort((x,y)=>{ return x.orderIndex>y.orderIndex?1:-1})"
                          :key="testCase.id"
                          @click="selectTestCase(testCase, testSuit, task)"
                          :ref="task.id + '_' + testSuit.id + '_' + testCase.id">
              <template slot="title">
                <el-tooltip v-if="testCase.name.length > 17"
                            :content="testCase.name"
                            placement="right"
                            effect="light">
                  <i class="el-icon-document"></i>
                  <span>{{
                    testCase.name.length > 17
                      ? testCase.name.substr(0, 17) + '...'
                      : testCase.name
                  }}</span>
                </el-tooltip>
                <span v-else>{{
                    testCase.name
                  }}</span>
                <el-tooltip content="拷贝用例"
                            placement="right"
                            effect="light">
                  <i class="el-icon-document"></i>
                  <span>
                    <el-button type="text"
                               icon="el-icon-document-copy"
                               @click.stop="setCopyTestCase(testCase)"
                               style="margin-bottom: 5px"></el-button>
                  </span>
                </el-tooltip>
              </template>
            </el-menu-item>
            <el-menu-item>
              <template>
                <el-button type="text"
                           @click.stop="showAddForTestCase(testSuit.id, task.id)">
                  <i class="el-icon-folder-add"
                     style="color: #67c23a; font-size: 14px">
                    添加用例
                  </i>
                </el-button>
                <el-button type="text"
                           @click.stop="pasteTestCase(testSuit, task)"
                           v-if="copyTestCase"
                           style="margin-left: 60px">
                  <i class="el-icon-printer"
                     style="color: #67c23a; font-size: 14px">
                    粘贴用例
                  </i>
                </el-button>
              </template>
            </el-menu-item>
          </el-submenu>
          <el-menu-item @click="showEditForTestSuit(null, task.id)">
            <template slot="title">
              <div>
                <i class="el-icon-folder-add"
                   style="color: #67c23a; font-size: 18px">
                  <span style="font-size: 14px"> 添加套件</span></i>
              </div>
            </template>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </div>
    <el-dialog :visible.sync="editVisibleForTestSuit"
               :title="currentTestSuitId ? '编辑测试套件' : '添加测试套件'"
               center
               append-to-body>
      <test-suit-edit :id="currentTestSuitId"
                      :taskId="currentTaskId"
                      @callbackForSave="callbackForSaveForTestSuit"
                      @callbackForCancel="callbackForCancelForTestSuit"
                      v-if="editVisibleForTestSuit"></test-suit-edit>
    </el-dialog>
    <el-dialog :visible.sync="editVisibleForTestCase"
               title="添加测试用例"
               center
               append-to-body>
      <test-case-add :testSuitId="currentTestSuitId"
                     :taskId="currentTaskId"
                     @callbackForSave="callbackForSaveForTestCase"
                     @callbackForCancel="callbackForCancelForTestCase"
                     v-if="editVisibleForTestCase"></test-case-add>
    </el-dialog>
  </div>
</template>

<script>
import TestSuitEdit from '@/components/testsuit/TestSuitEdit'
import TestCaseAdd from '@/components/testcase/TestCaseAdd'
import * as taskApi from '@/api/task'
import * as testcaseApi from '@/api/testcase'
import * as testsuitApi from '@/api/testsuit'

export default {
  name: 'TestCaseList',
  components: {
    TestSuitEdit,
    TestCaseAdd,
  },
  data () {
    return {
      editVisibleForTestSuit: false,
      editVisibleForTestCase: false,
      currentTestSuitId: null,
      currentTaskId: null,
      defaultActive: "-1"
    }
  },
  computed: {
    //获取当前项目
    project () {
      return this.$store.getters.getCurrentProject
    },
    //获取当前任务详情列表，下面包括测试套件，测试套件下又包含测试用例
    taskDetails () {
      return this.$store.getters.getTaskDetails
    },
    //获取拷贝的测试用例，用于粘贴的时候
    copyTestCase () {
      return this.$store.getters.getCopyTestCase
    },
  },
  created () {
    this.getInitData()
  },
  methods: {
    getInitData () {
      this.getTaskDetails()
    },
    //获取任务详情列表
    getTaskDetails () {
      taskApi.queryDetailByProjectId(this.project.id).then((response) => {
        let result = response.data
        this.$store.commit('setTaskDetails', result.data)
      })
    },


    showEditForTestSuit (testSuitId, taskId) {
      this.currentTestSuitId = testSuitId
      this.currentTaskId = taskId
      this.editVisibleForTestSuit = true
    },
    hideEditForTestSuit () {
      this.editVisibleForTestSuit = false
    },
    callbackForSaveForTestSuit () {
      this.hideEditForTestSuit()
      this.getInitData()
    },
    callbackForCancelForTestSuit () {
      this.hideEditForTestSuit()
    },

    showAddForTestCase (testSuitId, taskId) {
      this.currentTestSuitId = testSuitId
      this.currentTaskId = taskId
      this.editVisibleForTestCase = true
    },
    hideAddForTestCase () {
      this.editVisibleForTestCase = false
    },
    callbackForSaveForTestCase (testCaseId, testSuitId, taskId) {
      this.hideAddForTestCase()
      this.getInitData()
      //触发点击事件
      let _refs = this.$refs
      setTimeout(function () {
        let item = _refs[taskId + '_' + testSuitId + '_' + testCaseId][0]
        item.$emit('click')
      }, 100)
    },
    callbackForCancelForTestCase () {
      this.hideAddForTestCase()
    },


    //选择测试用例，设置当前测试用例
    selectTestCase (testCase, testSuit, task) {
      //设置当前激活测试用例菜单
      this.defaultActive = task.id + '_' + testSuit.id + '_' + testCase.id
      this.$store.commit('setCurrentTestCase', testCase)
    },
    //删除测试套件
    removeTestSuit (testSuit) {
      this.$confirm('确定删除测试套件[' + testSuit.name + ']吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          testsuitApi.remove(testSuit.id).then((response) => {
            if (response.data.code === 0) {
              this.getInitData()
              this.$message({
                message: '删除成功。',
                type: 'success',
              })
            } else {
              this.$message({
                message: response.data.message,
                type: 'warning',
              })
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除',
          })
        })
    },
    //拷贝测试用例
    setCopyTestCase (testCase) {
      this.$store.commit('setCopyTestCase', testCase)
      this.$message({
        message: '拷贝成功。',
        type: 'success',
      })
    },
    //粘贴测试用例
    pasteTestCase (testSuit, task) {
      let copyTestCase = JSON.parse(JSON.stringify(this.copyTestCase))
      //转换成字符串
      if (typeof copyTestCase.extract == 'object') {
        copyTestCase.extract = JSON.stringify(copyTestCase.extract)
      }
      if (typeof copyTestCase.assertion == 'object') {
        copyTestCase.assertion = JSON.stringify(copyTestCase.assertion)
      }
      if (typeof copyTestCase.dbAssertion == 'object') {
        copyTestCase.dbAssertion = JSON.stringify(copyTestCase.dbAssertion)
      }
      copyTestCase.testSuitId = testSuit.id
      copyTestCase.taskId = task.id
      testcaseApi.copy(copyTestCase).then((response) => {
        if (response.data.code === 0) {
          this.getInitData()
          //触发点击事件
          let _refs = this.$refs
          setTimeout(function () {
            _refs[
              task.id + '_' + testSuit.id + '_' + response.data.data.id
            ][0].$emit('click')
          }, 100)
          this.$message({
            message: '粘贴成功。',
            type: 'success',
          })
        } else {
          this.$message({
            message: response.data.message,
            type: 'warning',
          })
        }
      })
    },
  },
}
</script>

<style scoped>
</style>