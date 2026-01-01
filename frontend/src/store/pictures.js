import { reactive } from 'vue'

export const pictures = reactive({
  list: [],
  add(picture) {
    this.list.push(picture)
  },
  remove(picture) {
    this.list = this.list.filter(p => p !== picture)
  }
})