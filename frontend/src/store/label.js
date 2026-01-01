import { reactive } from "vue";

export const labelStore = reactive({
  labels: ['风景', '动物', '城市', '人物', '科技', '艺术'],
  addLabel(label) {
    if (label && !this.labels.includes(label)) {
      this.labels.push(label);
    }
  },
  removeLabel(label) {
    const index = this.labels.indexOf(label);
    if (index !== -1) {
      this.labels.splice(index, 1);
    }
  }
});