<template>
  <el-container style="height:100vh">
    <el-header style="background-color: #409eff; height: 40px;">
      <router-link to="/">首页</router-link> |
      <router-link to="/login">登录</router-link> |
      <router-link to="/register">注册</router-link> |
      <router-link to="/editimage">编辑图片</router-link>
    </el-header>

    <div class="user-area">
      <el-avatar :size="36">{{ initial }}</el-avatar>
      <span class="user-name">{{ userNameDisplay }}</span>
    </div>

    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<script>
import { computed } from 'vue'
import { userStore } from './store/user'
import router from './router';
export default {
  name: 'App',
  setup() {
    const userNameDisplay = computed(() => userStore.username || '未登录')
    const initial = computed(() => {
      return userStore.username ? userStore.username.charAt(0).toUpperCase() : '未'
    })
    return { userNameDisplay, initial }
  }
}
</script>

<style scoped>
.user-area {
  position: absolute;
  top: 8px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: hwb(0 0% 100%);
  font-size: 14px;
}

/* Element Plus Avatar 调整 */
.user-area .el-avatar {
  background-color: #fff;
  color: #409eff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
  font-weight: 600;
}

.user-name {
  min-width: 60px;
  text-align: left;
}
</style>