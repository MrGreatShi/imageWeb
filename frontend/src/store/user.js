import { reactive } from 'vue'

// 响应式全局用户状态
export const userStore = reactive({
  id: 0,
  username: '',
  email: '',
  pathToImage: '',
  clear(){
    this.id = 0
    this.username = ''
    this.email = ''
    this.pathToImage = ''
  }
})

// 导出配置常量
export const WebsiteConfig = 'http://localhost:8080'
export const ImageRepositoryHeader = WebsiteConfig + '/imageRepository/'
