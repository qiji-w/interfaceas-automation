<template>
  <div class="project-edit">
    <el-form :model="project"
             ref="project"
             label-width="80px">
      <el-form-item prop="description"
                    label="项目名称">
        <el-input v-model.trim="project.name"
                  placeholder="请输入项目名称"></el-input>
      </el-form-item>
      <el-form-item prop="description"
                    label="项目描述">
        <el-input v-model.trim="project.description"
                  placeholder="请输入项目描述"></el-input>
      </el-form-item>
      <el-form-item label="负责人">
        <el-select v-model="leader"
                   value-key="id"
                   placeholder="请选择负责人"
                   style="width: 100%;">
          <el-option v-for="item in users"
                     :key="item.id"
                     :label="item.name"
                     :value="item">
          </el-option>
        </el-select>
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
import * as userApi from '@/api/user'
import * as projectApi from '@/api/project'
export default {
  name: 'ProjectEdit',
  props: {
    id: Number,
  },
  data () {
    return {
      users: [],
      leader: {},
      project: {},
    }
  },
  created () {
    this.getInitData()
  },
  methods: {
    async getInitData () {
      await userApi.queryAll().then((response) => {
        let result = response.data
        this.users = result.data
      })
      if (this.id) {
        await projectApi.getById(this.id).then((response) => {
          let result = response.data
          this.project = result.data
          this.leader = {
            id: this.project.leaderId,
            name: this.project.leaderName,
          }
        })
      }
    },
    save () {
      this.project.leaderId = this.leader.id
      this.project.leaderName = this.leader.name
      let operation = null
      if (!this.project.id) {
        operation = projectApi.create(this.project)
      } else {
        operation = projectApi.modify(this.project)
      }
      operation.then((response) => {
        if (response.data.code === 0) {
          this.$message({
            message: '保存成功。',
            type: 'success',
          })
          this.$emit('callbackForSave')
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
.dialog-footer {
  text-align: right;
}
</style>