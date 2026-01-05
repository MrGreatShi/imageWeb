<template>
  <el-container style="height:100vh">
    <el-header style="background-color: #409eff; height: 40px;"> |
      <router-link to="/">首页</router-link> |
      <router-link to="/editimage">编辑图片</router-link> |
    </el-header>

    <el-popover placement="bottom-end" width="160" trigger="click">
      <div style="display:flex;flex-direction:column;gap:8px;padding:8px;">
        <el-button type="primary" v-if="userStore.username === ''" >
          <router-link to="/login">登录</router-link>
        </el-button>
        <el-button type="warning" @click="logout" v-if="userStore.username !== ''">注销</el-button>
      </div>
      <template #reference>
        <div class="user-area">
          <el-avatar :size="36" class="clickable">{{ avatarInitials}}</el-avatar>
          <span class="user-name">{{usernameInitials}}</span>
        </div>
      </template>
    </el-popover>

    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<script>
import { computed } from 'vue'
import { userStore } from './store/user'
import router from './router';
import {ElMessageBox} from "element-plus";
export default {
  name: 'App',
  computed: {
    avatarInitials() {
      if (userStore.username === '') return '未';
      return userStore.username.charAt(0).toUpperCase();
    },
    usernameInitials() {
      if (userStore.username === '') return '未登录';
      return userStore.username;
    },
    userStore() {
      return userStore
    }
  },
  methods: {
    logout() {
      ElMessageBox({
        title: '确认注销',
        message: '您确定要注销吗？',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
      }).then(() => {
        userStore.id = 0;
        userStore.username = '';
        userStore.email = '';
        userStore.pathToImage = '';
        router.push('/login');
      }).catch(() => {
        // 取消操作
      });

    }
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