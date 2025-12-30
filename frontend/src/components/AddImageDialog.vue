<template>
  <div class="AddImageDialog">
    <el-row :gutter="16">
      <el-col :span="12">
        <div class="uploader">
          <div class="preview" v-if="src">
            <img :src="src" alt="preview" />
          </div>
          <div class="preview placeholder" v-else>
            <div>请选择图片或拖拽到此处</div>
          </div>

          <div class="actions">
            <input ref="fileInput" type="file" accept="image/*" class="hidden-file" @change="onFileChange" />
            <el-button @click="chooseFile">选择图片</el-button>
            <el-button type="danger" @click="clear" :disabled="!src">清除</el-button>
          </div>
        </div>
      </el-col>

      <el-col :span="12">
        <el-form label-position="top" :model="form">
          <el-form-item label="图片标题">
            <el-input v-model="form.title" placeholder="请输入图片标题" />
          </el-form-item>

          <el-form-item label="标签">
            <el-select
              v-model="form.labels"
              multiple
              filterable
              allow-create
              clearable
              placeholder="请选择或新建标签"
            >
              <el-option
                v-for="label in labelStore.labels"
                :key="label"
                :label="label"
                :value="label"
              />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="onAiGenerate">AI 生成标签</el-button>
            <span class="hint">（仅界面，未实现）</span>
          </el-form-item>

          <div class="footer-actions">
            <el-button @click="onCancel">取消</el-button>
            <el-button type="primary" @click="onConfirm" :disabled="!src">确定</el-button>
          </div>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onUnmounted } from 'vue'
import { labelStore } from '../store/label'

export default {
  name: 'AddImageDialog',
  setup(_, { emit }) {
    const fileInput = ref(null)
    const src = ref('')
    const fileObj = ref(null)

    const form = reactive({ title: '', labels: [] })

    function chooseFile() {
      fileInput.value && fileInput.value.click()
    }

    function onFileChange(e) {
      const f = e.target.files && e.target.files[0]
      if (!f) return
      if (fileObj.value && fileObj.value !== f) {
        // 释放旧 URL
        releaseObjectURL()
      }
      fileObj.value = f
      src.value = URL.createObjectURL(f)
    }

    function clear() {
      if (fileInput.value) fileInput.value.value = ''
      fileObj.value = null
      releaseObjectURL()
      src.value = ''
      form.title = ''
      form.labels = []
    }

    function onAiGenerate() {
      // 仅界面按钮，实际 AI 功能未实现
      // 可在此打开提示或调用后端/AI 服务
      window.alert('AI 生成标签功能未实现（仅界面）')
    }

    function onConfirm() {
      if (!src.value) return

      // 将新标签加入 labelStore（如果 labelStore 为可变数组）
      try {
        form.labels.forEach(l => {
          if (!labelStore.labels.includes(l)) labelStore.labels.push(l)
        })
      } catch (e) {
        // 忽略 store 更新错误
      }

      emit('add', { src: src.value, title: form.title, labels: [...form.labels] })
    }

    function onCancel() {
      emit('close')
    }

    function releaseObjectURL() {
      try {
        if (src.value && src.value.startsWith('blob:')) URL.revokeObjectURL(src.value)
      } catch (e) {}
    }

    onUnmounted(() => {
      releaseObjectURL()
    })

    return { fileInput, chooseFile, onFileChange, src, form, clear, onAiGenerate, onConfirm, onCancel, labelStore }
  }
}
</script>

<style scoped>
.add-image-dialog {
  min-width: 640px;
}
.uploader {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.preview {
  width: 100%;
  height: 320px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  overflow: hidden;
}
.preview img {
  max-width: 100%;
  max-height: 100%;
  display: block;
}
.preview.placeholder {
  color: #999;
}
.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.hidden-file { display: none; }
.footer-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.hint { margin-left: 8px; color: #999; font-size: 12px; }
</style>
