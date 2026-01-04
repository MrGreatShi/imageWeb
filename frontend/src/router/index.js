// createRouter用来新建路由实例，createWebHashHistory用来配置我们内容使用hash的模式（也就是路径上会通过#来区分）
import { createRouter, createWebHashHistory } from 'vue-router'
import Home from '../pages/home.vue'
import Register from '../pages/register.vue'
 
const routes = [
  {
    path: '/',
    name: 'Home',
    component: ()=> import('../pages/home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../pages/register.vue')
  },{
    path: '/editimage',
    name: 'EditImage',
    component: () => import('../pages/editImage.vue')
  },{
    path:'/help',
    name:'Help',
    component: () => import('../help/help.md')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})
 
export default router