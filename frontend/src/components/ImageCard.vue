<template>
  <div class="image-card">
    <el-image
      :src="src"
      fit="contain"
      :preview-src-list="[src]"
      class="thumb"
      :style="{ height: imageHeight }"
    ></el-image>
    <el-row>
      <div class="title">{{title}}</div>
      <el-button size="mini" type="danger" @click.stop="$emit('delete')">删除</el-button>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'ImageCard',
  props: {
    itemKey: { type: [Number, String], required: false },
    src: { type: String, required: true },
    title: { type: String, default: '' },
    height: { type: [Number, String], default: 180 }
  },
  methods: {
    async onclickdel(key) {
      this.$emit('delete', key);
    }
  },
  computed: {
    imageHeight() {
      return typeof this.height === 'number' ? `${this.height}px` : this.height
    }
  }
}
</script>

<style scoped>
.image-card {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
  display: flex;
  flex-direction: column;
}
.thumb {
  width: 100%;
  height: 180px;
  background: #f7f7f7;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}
.thumb ::v-deep(.el-image__img) {
  /* 保持纵横比并居中显示在容器中 */
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
  object-position: center;
  display: block;
}
.title {
  padding: 10px;
  font-size: 14px;
  color: #333;
  text-align: left;
  background: transparent;
}
</style>
