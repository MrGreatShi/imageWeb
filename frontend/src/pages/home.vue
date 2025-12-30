<template>
  <el-container style="height: 40vh">
    <div>图片展示区</div>
  </el-container>

  <el-form>
    <el-row :gutter="12">
      <el-col :xs="24" :sm="8">
        <el-form-item label="图片地址">
          <el-input v-model="imageUrl" placeholder="请输入图片地址" />
        </el-form-item>
      </el-col>

      <el-col :xs="24" :sm="8">
        <el-form-item label="图片标题">
          <el-input v-model="imageTitle" placeholder="请输入图片标题" />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row>
      <el-col :xs="24" :sm="8">
        <el-form-item label="标签">
          <el-select v-model="selectedLabels" multiple clearable placeholder="请选择标签">
            <el-option
              v-for="label in labelStore.labels"
              :key="label"
              :label="label"
              :value="label"
            />
          </el-select>
        </el-form-item>
      </el-col>

      <el-col :xs="24" :sm="8">
        <el-button type="primary" @click="searchImage">搜索图片</el-button>
      </el-col>
    </el-row>

    <el-form-item>
      <el-button type="primary" @click="openAddDialog">添加图片</el-button>
    </el-form-item>
  </el-form>

  <div class="home-page">
    <h2>我的图片</h2>
    <div class="gallery">
      <ImageCard
        v-for="(src, idx) in pictureUrls"
        :key="idx"
        :src="src"
        :title="filenames[idx]"
        height="140"
      />
    </div>
  </div>

  <el-dialog v-model="showAddDialog" width="720px" append-to-body>
    <AddImageDialog @add="onAddImage" @close="showAddDialog = false" />
  </el-dialog>
</template>

<script>
import { ref } from 'vue'
import ImageCard from '../components/ImageCard.vue'
import AddImageDialog from '../components/AddImageDialog.vue'
import { labelStore } from '../store/label'
export default {
  name: 'HomePage',
  components: { ImageCard, AddImageDialog },
  setup() {
    const imageUrl = ref('')
    const imageTitle = ref('')
    const selectedLabels = ref([])
    const showAddDialog = ref(false)

    const filenames = [
      'pic1.jfif','pic10.webp','pic2.jpg','pic3.webp','pic4.jpg',
      'pic5.webp','pic6.webp','pic7.webp','pic8.webp','pic9.webp'
    ]
    const pictureUrls = ref(filenames.map(n => `/pictures/${n}`))

    function addImage() {
      // 将外部 URL 添加到画廊最前面
      if (!imageUrl.value) return
      pictureUrls.value.unshift(imageUrl.value)
      imageUrl.value = ''
      imageTitle.value = ''
      selectedLabels.value = []
    }

    function onAddImage(payload) {
      // payload = { src, title, labels }
      if (!payload || !payload.src) return
      pictureUrls.value.unshift(payload.src)
      filenames.unshift(payload.title || '新图片')
      showAddDialog.value = false
    }

    function searchImage() {
      // 占位：实现搜索逻辑
    }

    function openAddDialog() {
      console.log('打开添加图片对话框')
      showAddDialog.value = true
    }

    return { imageUrl, imageTitle, filenames, pictureUrls, addImage, searchImage, selectedLabels, labelStore, showAddDialog, onAddImage, openAddDialog }
  }
}
</script>

<style scoped>
.home-page {
  width: 100%;
}
.gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
  margin-top: 12px;
}
</style>