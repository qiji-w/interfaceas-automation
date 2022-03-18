<template>
  <div class="module-edit">
    <el-form :model="module">
      <el-form-item>
        <el-input v-model="module.name"
                  placeholder="请输入模块名">
          <template slot="prepend">模块名</template>
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
import * as moduleApi from '@/api/module'

export default {
  name: 'ModuleEdit',
  props: {
    id: Number,
  },
  data () {
    return {
      module: {},
    }
  },
  computed: {
    project () {
      return this.$store.getters.getCurrentProject
    },
  },
  created () {
    this.getInitData()
  },
  methods: {
    getInitData () {
      if (this.id) {
        moduleApi.getById(this.id).then((response) => {
          let result = response.data
          this.module = result.data
        })
      }
    },
    save () {
      this.module.projectId = this.project.id
      let operation = null
      if (!this.module.id) {
        operation = moduleApi.create(this.module)
      } else {
        operation = moduleApi.modify(this.module)
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
</style>