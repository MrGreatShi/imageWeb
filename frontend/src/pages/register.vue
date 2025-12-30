<template>
  <div class="register-card">
    <h2>注册</h2>
    <el-form :model="form" ref="registerForm" label-position="top" @submit.prevent="handleRegister">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" type="email" placeholder="请输入邮箱"></el-input>
      </el-form-item>

      <div class="actions">
        <el-button type="primary" @click="handleRegister">注册</el-button>
        <router-link to="/login" class="btn link">去登录</router-link>
      </div>

      <p v-if="error" class="error">{{ error }}</p>
    </el-form>
  </div>
</template>

<script>
import { userStore } from '../store/user'
export default {
  name: 'RegisterPage',
  data() {
    return {
      form: {
        username: '',
        password: '',
        email: ''
      },
      error: ''
    }
  },
  methods: {
    handleRegister() {
      this.error = '';
      if (!this.form.username || !this.form.password || !this.form.email) {
        this.error = '所有字段都是必填的';
        return;
      }
      // 保存到全局 store
      userStore.username = this.form.username
      userStore.password = this.form.password
      userStore.email = this.form.email
      // 这里可以调用后端注册接口，当前仅作示例，注册成功后跳转到首页
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.register-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}
.form-group {
  margin-bottom: 12px;
}
label {
  display: block;
  font-size: 14px;
  margin-bottom: 6px;
}
input[type="text"], input[type="password"] {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
}
.actions {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-top: 10px;
}
.btn {
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #dcdcdc;
  background: #fff;
  text-decoration: none;
  color: #333;
}
.btn.primary {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
.btn.link {
  background: transparent;
  border-color: transparent;
  color: #409eff;
}
.error {
  color: #e74c3c;
  margin-top: 8px;
}
</style>