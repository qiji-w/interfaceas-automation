<template>
  <div class="test-suit">
    <el-form :model="testsuit">
      <el-form-item>
        <el-input v-model="testsuit.name">
          <template slot="prepend">套件名</template>
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
import * as testsuitApi from '@/api/testsuit'

export default {
  name: 'TestSuit',
  props: {
    id: Number,
    taskId: Number
  },
  data () {
    return {
      testsuit: {},
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
        testsuitApi.getById(this.id).then((response) => {
          let result = response.data
          this.testsuit = result.data
        })
      }
    },
    save () {
      this.testsuit.projectId = this.project.id
      this.testsuit.taskId = this.taskId
      let operation = null
      if (!this.testsuit.id) {
        operation = testsuitApi.create(this.testsuit)
      } else {
        operation = testsuitApi.modify(this.testsuit)
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