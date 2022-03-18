<template>
  <div class="environment">
    <el-card
      class="box-card"
      style="height: 450px; box-shadow: 0 0 5px rgb(85, 170, 255)"
    >
      <el-row :gutter="20" style="line-height: 40px">
        <el-col :span="12">
          <h4>项目环境</h4>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-plus"
            @click="showEdit(null)"
            plain
          >
            添加环境
          </el-button>
        </el-col>
      </el-row>
      <div class="scro">
        <el-collapse>
          <el-collapse-item
            :title="env.name"
            :name="env.id"
            v-for="env in environments"
            :key="env.id"
          >
            <template slot="title">
              <span style="font-weight: bold; width: 150px">{{
                env.name
              }}</span>
              <span style="font-weight: bold; color: #20a0ff">{{
                env.host
              }}</span>
            </template>
            <el-card style="box-shadow: 0 0 5px rgb(85, 170, 255); width: 90%">
              <el-row :gutter="20">
                <el-col :span="16">
                  <div>
                    <b
                      >服务器地址 :
                      <span style="color: #797979">{{ env.host }}</span></b
                    >
                  </div>
                  <div><b>数据库配置:</b></div>
                  <div
                    style="text-indent: 40px; font-weight: bold; color: #797979"
                  >
                    <div>host : {{ JSON.parse(env.dbConfig).host }}</div>
                    <div>user : {{ JSON.parse(env.dbConfig).user }}</div>
                    <div>
                      password : {{ JSON.parse(env.dbConfig).password }}
                    </div>
                    <div>db : {{ JSON.parse(env.dbConfig).db }}</div>
                    <div>charset : {{ JSON.parse(env.dbConfig).charset }}</div>
                    <div>
                      autocommit : {{ JSON.parse(env.dbConfig).autocommit }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div style="text-align: center">
                    <div style="margin: 20px auto">
                      <el-button
                        type="primary"
                        plain
                        size="mini"
                        @click="showEdit(env.id)"
                        >修改环境</el-button
                      >
                    </div>
                    <div>
                      <el-button
                        type="danger"
                        plain
                        size="mini"
                        @click="remove(env.id)"
                        >删除环境</el-button
                      >
                    </div>
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-card>
    <el-dialog
      :visible.sync="editVisible"
      :title="currentId ? '编辑项目环境' : '添加项目环境'"
      center
    >
      <environment-edit
        :id="currentId"
        @callbackForSave="callbackForSave"
        @callbackForCancel="callbackForCancel"
        v-if="editVisible"
      ></environment-edit>
    </el-dialog>
  </div>
</template>

<script>
import EnvironmentEdit from '@/components/environment/EnvironmentEdit'
import * as environmentApi from '@/api/environment'

export default {
  name: 'Environment',
  components: {
    EnvironmentEdit,
  },
  data() {
    return {
      environments: [],
      editVisible: false,
      currentId: null,
    }
  },
  computed: {
    project() {
      return this.$store.getters.getCurrentProject
    },
  },
  created() {
    this.getInitData()
  },
  methods: {
    getInitData() {
      environmentApi.queryByProjectId(this.project.id).then((response) => {
        let result = response.data
        this.environments = result.data
        this.$emit('callback',this.environments.length)
      })
    },
    showEdit(id) {
      this.currentId = id
      this.editVisible = true
    },
    hideDetail() {
      this.editVisible = false
    },
    callbackForSave() {
      this.hideDetail()
      this.getInitData()
    },
    callbackForCancel() {
      this.hideDetail()
    },
    remove(id) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          environmentApi.remove(id).then((response) => {
            if (response.data.code === 0) {
              this.$message({
                message: '删除成功。',
                type: 'success',
              })
              this.getInitData()
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
  },
}
</script>

<style scoped>
</style>