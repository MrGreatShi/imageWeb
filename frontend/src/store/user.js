import { reactive } from 'vue'

// 全局用户状态（简单共享状态）
export const userStore = reactive({
  username: '',
  password: '',
  email: ''
})
