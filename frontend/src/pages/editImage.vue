<template>
  <div class="edit-page">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="16">
        <div class="canvas-area">
          <!-- 裁剪模式：显示一个固定的裁剪窗口（演示用） -->
          <div v-if="isCropping" class="crop-area" :style="cropBoxStyle">
            <img :src="src" class="preview-img" :style="previewStyle" />
          </div>

          <!-- 普通预览 -->
          <div v-else class="preview-wrapper">
            <img :src="src" class="preview-img" :style="previewStyle" />
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="8">
        <el-card class="control-card" shadow="hover">
          <div class="control-group">
            <div class="control-row">
              <el-button type="primary" @click="toggleCrop">{{ isCropping ? '退出裁剪' : '裁剪' }}</el-button>
              <el-button @click="applyCrop" :disabled="!isCropping">应用裁剪</el-button>
            </div>

            <div class="control-row">
              <el-button @click="rotate(-90)">左转 90°</el-button>
              <el-button @click="rotate(90)">右转 90°</el-button>
            </div>

            <div class="control-row">
              <div class="label">色相调整</div>
              <el-slider v-model="hue" :min="-180" :max="180" show-input @change="applyHue" />
            </div>

            <div class="control-row">
              <div class="label">亮度</div>
              <el-slider v-model="brightness" :min="0" :max="200" show-input @change="applyHue" />
            </div>

            <div class="control-row">
              <div class="label">对比度</div>
              <el-slider v-model="contrast" :min="0" :max="200" show-input @change="applyHue" />
            </div>

            <div class="control-row">
              <el-button @click="reset">重置</el-button>
            </div>

            <div class="control-row">
              <el-button @click="chooseFile">更换图片</el-button>
              <input type="file" ref="fileInput" @change="onFileChange" style="display: none;" />
            </div>

            <div class="spacer" />

          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 右下角固定保存按钮（未实现保存逻辑，仅 UI） -->
    <el-button type="success" class="save-btn" @click="onSave">保存</el-button>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'EditImagePage',
  setup() {
    // 演示用图片，真实场景可从路由或 store 传入
    const src = ref('/pictures/pic1.jfif')

    const isCropping = ref(false)
    const rotation = ref(0) // deg
    const hue = ref(0) // deg
    const brightness = ref(100) // percent
    const contrast = ref(100) // percent
    const fileInput = ref(null)

    // 预览样式，基于当前变换和滤镜
    const previewStyle = computed(() => {
      const t = `transform: rotate(${rotation.value}deg);`
      const f = `filter: hue-rotate(${hue.value}deg) brightness(${brightness.value}%) contrast(${contrast.value}%);`
      return `${t} ${f}`
    })

    // 裁剪框样式（演示为固定尺寸，可扩展为可拖拽调整）
    const cropBoxStyle = computed(() => ({
      width: '480px',
      height: '320px',
      overflow: 'hidden',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      border: '1px dashed rgba(0,0,0,0.15)',
      background: '#fff'
    }))

    function toggleCrop() {
      isCropping.value = !isCropping.value
    }

    function applyCrop() {
      // 这里只是演示：真实裁剪需要 canvas 操作并替换 src
      // 此处可提示或产生一个临时裁剪预览
      // 暂不实现真正保存
      isCropping.value = false
    }

    function rotate(deg) {
      rotation.value = (rotation.value + deg) % 360
    }

    function applyHue() {
      // 变化已通过 computed 反映到预览样式
    }

    function reset() {
      rotation.value = 0
      hue.value = 0
      brightness.value = 100
      contrast.value = 100
      isCropping.value = false
    }

    function chooseFile() {
      if (fileInput.value) fileInput.value.click()
    }

    function onFileChange(e) {
      const f = e.target.files && e.target.files[0]
      if (!f) return
      // 使用临时 URL 预览本地文件
      const url = URL.createObjectURL(f)
      src.value = url
    }

    function onSave() {
      // 保存功能未实现，先做 UI 反馈
      // 你可以在这里打开一个提示或调用后端接口
      // 暂时使用浏览器 alert 表示
      window.alert('已触发保存（保存功能尚未实现）')
    }

    return {
      src,
      isCropping,
      rotation,
      hue,
      brightness,
      contrast,
      previewStyle,
      cropBoxStyle,
      toggleCrop,
      applyCrop,
      rotate,
      applyHue,
      reset,
      fileInput,
      chooseFile,
      onFileChange,
      onSave
    }
  }
}
</script>

<style scoped>
.edit-page {
  padding: 16px;
}
.canvas-area {
  background: #fafafa;
  min-height: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.04);
}
.preview-wrapper, .crop-area {
  display: flex;
  align-items: center;
  justify-content: center;
}
.preview-img {
  max-width: 100%;
  max-height: 100%;
  display: block;
  transition: transform 0.2s ease, filter 0.2s ease;
}
.control-card {
  height: 100%;
}
.control-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.control-row {
  display: flex;
  gap: 8px;
  align-items: center;
}
.label {
  width: 60px;
  color: #666;
  font-size: 13px;
}
.note {
  font-size: 12px;
  color: #999;
}
.spacer {
  flex: 1;
}
.save-btn {
  position: fixed;
  right: 20px;
  bottom: 20px;
}
</style>
