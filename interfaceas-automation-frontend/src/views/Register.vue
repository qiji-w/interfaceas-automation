<template>
  <div class="login_context">
    <div class="login_box">
      <el-button type="primary"
                 plain
                 style="width: 20%; float: right; margin-top: 10px; margin-right: 10px"
                 @click="$router.push('/login')">登&nbsp;&nbsp;&nbsp;录</el-button>
      <div class="title">
        接口自动化测试平台<span style="color: #e6a23c"></span>
      </div>
      <el-form ref="loginRef"
               class="login_from"
               :model="user"
               :rules="rulesLogin">
        <el-form-item prop="username"
                      label="用户名">
          <el-input v-model="user.username"
                    prefix-icon="el-icon-user-solid"
                    placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password"
                      label="密码">
          <el-input type="password"
                    v-model="user.password"
                    placeholder="请输入密码"
                    prefix-icon="el-icon-s-goods"></el-input>
        </el-form-item>
        <el-form-item prop="name"
                      label="姓名">
          <el-input v-model="user.name"
                    prefix-icon="el-icon-user-solid"
                    placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item class="btns">
          <el-button type="warning"
                     size="medium"
                     style="width: 100%"
                     @click="register">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import * as account from '@/api/account.js'

export default {
  data () {
    return {
      user: {
        username: '',
        password: '',
      },
      status: false,
      rulesLogin: {
        username: [
          { required: true, message: '请输入登录用户名', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
        ],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
      },
    }
  },
  methods: {
    register () {
      account.register(this.user).then((response) => {
        let result = response.data

        if (result.code == 0) {
          this.$alert('注册成功，请使用注册信息到登录页进行登录使用。', '提示', {
            confirmButtonText: '确定',
            callback: () => {
              this.$router.push('/login')
            },
          })
        } else {
          this.$message({
            message: result.message,
            type: 'error',
          })
        }
      })
    },
  },
}
</script>

<style scoped>
.title {
  position: absolute;
  left: 50%;
  top: 25%;
  font-size: 24px;
  line-height: 50px;
  text-align: center;
  transform: translate(-50%, -50%);
  font-weight: bold;
  color: #409eff;
}

.login_context {
  background: rgb(255, 255, 255);
  height: 100%;
}
.login_box {
  width: 620px;
  height: 500px;
  box-shadow: 0 0 10px rgb(21, 21, 90);
  border-radius: 3px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.login_from {
  position: absolute;
  width: 100%;
  bottom: 0;
  padding: 5% 10%;
  box-sizing: border-box;
}

.btns {
  width: 100%;
  position: flex;
  float: right;
  justify-content: flex-end;
}
</style>
