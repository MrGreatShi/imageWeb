import { reactive, watch } from 'vue'

const STORAGE_KEY = 'userStore_v1'

// 默认值
const defaultState = {
  id: 0,
  username: '',
  email: '',
  pathToImage: ''
}

// 响应式全局用户状态
export const userStore = reactive({ ...defaultState })

// 从 localStorage 恢复
function loadUserStore() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return
    const data = JSON.parse(raw)
    // 只恢复已知字段，避免注入
    Object.assign(userStore, {
      id: Number(data.id || 0),
      username: data.username || '',
      email: data.email || '',
      pathToImage: data.pathToImage || ''
    })
  } catch (e) {
    console.warn('loadUserStore failed', e)
  }
}

// 保存到 localStorage（只保存必要字段）
export function saveUserStore() {
  try {
    const snapshot = {
      id: userStore.id,
      username: userStore.username,
      email: userStore.email,
      pathToImage: userStore.pathToImage
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(snapshot))
  } catch (e) {
    console.warn('saveUserStore failed', e)
  }
}

// 清空 store 与 localStorage（登出时调用）
export function clearUserStore() {
  Object.assign(userStore, { ...defaultState })
  try { localStorage.removeItem(STORAGE_KEY) } catch (e) {}
}

// 初始化：先恢复，再开始监听变化和跨标签同步
loadUserStore()

// 深度监听 store 变化并自动保存
watch(
    userStore,
    () => {
      saveUserStore()
    },
    { deep: true }
)

// 跨标签同步：当其它标签修改 localStorage 时同步到当前 store
window.addEventListener('storage', (e) => {
  if (e.key !== STORAGE_KEY) return
  try {
    const newVal = e.newValue ? JSON.parse(e.newValue) : null
    if (!newVal) {
      Object.assign(userStore, { ...defaultState })
    } else {
      Object.assign(userStore, {
        id: Number(newVal.id || 0),
        username: newVal.username || '',
        email: newVal.email || '',
        pathToImage: newVal.pathToImage || ''
      })
    }
  } catch (err) {
    console.warn('storage event parse failed', err)
  }
})

// 导出配置常量
export const WebsiteConfig = 'http://localhost:8080'
export const ImageRepositoryHeader = WebsiteConfig + '/imageRepository/'
