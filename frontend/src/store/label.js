import { reactive } from "vue";

export const labels = reactive({
  items: [
    { id: 1, title: '风景' },
    { id: 2, title: '动物' },
    { id: 3, title: '城市' },
    { id: 4, title: '人物' },
    { id: 5, title: '科技' },
    { id: 6, title: '艺术' },
  ],
  add(label) {
    if (!label) return;
    const title = typeof label === 'string' ? label : label.title;
    if (!this.items.find(l => l.title === title)) {
      this.items.push(typeof label === 'string' ? { id: Date.now(), title } : label);
    }
  },
  clear() {
    // 保持引用，使用 splice 清空
    this.items.splice(0, this.items.length);
  }
});