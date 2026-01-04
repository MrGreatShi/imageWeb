// 文件：`frontend/src/pages/login.vue`
<template>
  <div class="login-card">
    <h2>登录</h2>
    <el-form :model="form" ref="loginForm" label-position="top" @submit.prevent="handleLogin">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>

      <div class="actions">
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <router-link to="/register" class="btn link">去注册</router-link>
      </div>

      <p v-if="error" class="error">{{ error }}</p>
    </el-form>
  </div>
</template>

<script>
import {ImageRepositoryHeader, userStore, WebsiteConfig} from '../store/user'
import {ElMessageBox} from "element-plus";
export default {
  name: 'LoginPage',
  data() {
    return {
      form: {
        id: 0,
        username: '',
        email: ''
      },
      error: ''
    }
  },
  methods: {
    async handleLogin() {
      this.error = '';
      if (!this.form.username || !this.form.password) {
        this.error = '用户名和密码不能为空';
        return;
      }
      try {
        const url = WebsiteConfig + '/user/login';
        console.log('Login URL:', url);
        const resp = await fetch(url + `?username=${this.form.username}&password=${this.form.password}` , {
          method: 'GET'
        });

        if (!resp.ok) {
          const text = await resp.text().catch(() => '');
          this.error = text || `请求失败：${resp.status}`;
          return;
        }

        const data = await resp.json();

        ElMessageBox({
          title: '登录成功',
          message: '欢迎回来，' + data.username + '！',
          showConfirmButton: true,
        });

        userStore.id = data.id;
        userStore.username = data.username;
        userStore.email = data.email;
        userStore.pathToImage = ImageRepositoryHeader + data.username;

        await this.$router.push('/');
      } catch (err) {
        this.error = '登录失败，请稍后重试';
      }
    }
  }
}
</script>

<style scoped>
.login-card {
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