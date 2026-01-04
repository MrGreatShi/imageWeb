<template>
  <div class="AddImageDialog">
    <el-row :gutter="16">
      <el-col :span="12">
        <div class="uploader">
          <div class="preview" v-if="this.srcFile">
            <img :src="this.srcFile" alt="preview" />
          </div>
          <div class="preview placeholder" v-else>
            <div>请选择图片或拖拽到此处</div>
          </div>

          <div class="actions">
            <input ref="fileInput" type="file" accept="image/*" class="hidden-file" @change="onFileChange" />
            <el-button @click="chooseFile">选择图片</el-button>
            <el-button type="danger" @click="clear" :disabled="!this.srcFile">清除</el-button>
          </div>
        </div>
      </el-col>

      <el-col :span="12">
        <el-form label-position="top" >
          <el-form-item label="图片标题">
            <el-input v-model="imageTitle" placeholder="请输入图片标题" />
          </el-form-item>

          <el-form-item label="标签">
            <el-select
              v-model="imageLabels"
              multiple
              filterable
              allow-create
              clearable
              placeholder="请选择或新建标签"
            >
              <el-option
                v-for="label in labels.items"
                :key="label.id"
                :label="label.title"
                :value="label.id"
              /><el-button type="primary" @click = onAddNewLabel>新建标签</el-button>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="onAiGenerate">AI 生成标签</el-button>
          </el-form-item>

          <div class="footer-actions">
            <el-button @click="onCancel">取消</el-button>
            <el-button type="primary" @click="onConfirm" :disabled="!this.srcFile">确定</el-button>
          </div>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { onUnmounted } from 'vue';
import { userStore, WebsiteConfig } from "../store/user.js";
import { labels } from "../store/label";
import { ElMessageBox, ElMessage } from "element-plus";

export default {
  name: 'AddImageDialog',
  computed: {
    labels() { return labels; }
  },
  data() {
    return {
      imageTitle: '',
      imageLabels: [],
      srcFile: '',
      selectedFile: null,
      suggestions: [],
    }
  },
  methods: {
    chooseFile() { this.$refs.fileInput.click(); },
    onFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;
        this.srcFile = URL.createObjectURL(file);
      }
    },
    clear() {
      this.imageTitle = null;
      this.imageLabels = [];
      this.srcFile = null;
      this.selectedFile = null;
      if (this.$refs.fileInput) this.$refs.fileInput.value = '';
    },
    suggetedLabels(ids) {
      if (!Array.isArray(ids)) return [];
      return labels.items.filter(label => ids.includes(label.id)).map(label => label.id);
    },
    async onAiGenerate() {
      if (!this.selectedFile) {
        this.$message.warning('请先选择图片再使用 AI 生成标签');
        return;
      }
      const url = WebsiteConfig + '/ai/suggestLabels';
      const form = new FormData();
      form.append('file', this.selectedFile);
      form.append('user_id', userStore.id);
      try {
        const loading = ElMessage({ message: '正在识别标签…', type: 'info', duration: 0 });
        const resp = await fetch(url, { method: 'POST', body: form });
        loading.close && loading.close();
        if (!resp.ok) {
          const txt = await resp.text().catch(()=>'');
          ElMessageBox({ title: '错误', message: txt || `请求失败：${resp.status}`, showConfirmButton: true });
          return;
        }
        const data = await resp.json();
        console.log(data);
        this.imageLabels = this.suggetedLabels(data);
        ElMessage({ message: 'AI 已为你匹配标签', type: 'success', duration: 2000 });
      } catch (err) {
        console.error(err);
        ElMessage({ message: 'AI 标签生成失败，请稍后重试', type: 'error' });
      }
    },
    onCancel() { this.$emit('close'); },
    async onConfirm() {
      if (!this.imageTitle || !this.selectedFile) {
        ElMessageBox({ title: '错误', message: '请填写图片标题并选择图片', showConfirmButton: true });
        return;
      }

      try {
        const url = WebsiteConfig + '/image/store';
        const form = new FormData();
        form.append('file', this.selectedFile);
        form.append('user_id', userStore.id);
        form.append('title', this.imageTitle);
        // 传标签为 JSON 字符串，后端兼容解析
        form.append('labels', JSON.stringify(this.imageLabels));
        const resp = await fetch(url, {
          method: 'POST',
          body: form
        });

        if (!resp.ok) {
          const text = await resp.text().catch(()=> '');
          ElMessageBox({ title: '错误', message: text || `请求失败：${resp.status}`, showConfirmButton: true });
          return;
        }

        ElMessageBox({ title: '成功', message: '图片添加成功！', showConfirmButton: true });
        this.$emit('add');
        this.$emit('close');
        this.imageTitle = null;
        this.imageLabels = [];
        this.srcFile = null;
        this.selectedFile = null;

      } catch (err) {
        console.error(err);
        ElMessageBox({ title: '错误', message: '上传失败，请稍后重试', showConfirmButton: true });
      }
    },
    async onAddNewLabel() {
      ElMessageBox.prompt('请输入新标签名称', '添加新标签', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '标签不能为空'
      }).then(async ({value}) => {
        const url = WebsiteConfig + '/label/addToUser';
        const response = await fetch(url, {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({
            userId: userStore.id,
            title: value
          })
        })
        if (!response.ok) {
          const text = await response.text().catch(() => '');
          ElMessageBox({ title: '错误', message: text || `请求失败：${response.status}`, showConfirmButton: true });
          return;
        }
        else{
          ElMessage({ message: '添加标签成功', type: 'success', duration: 2000 });
        }
        this.$emit('add');
      }).catch(() => {
        // 用户取消或关闭消息框，不做处理
      });
    }
  },
  mounted() {},
  unmounted() {
    if (this.srcFile) URL.revokeObjectURL(this.srcFile);
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
