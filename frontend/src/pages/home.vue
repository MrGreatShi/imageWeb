<template>
  <div class="carousel-wrapper">
    <el-carousel height="330px" indicator-position="outside" arrow="always" :autoplay="true" :interval="3200">
      <el-carousel-item v-for="(img, idx) in carouselImages" :key="idx">
        <div class="carousel-image-box">
          <img :src="img.path" :alt="img.title" class="carousel-image" />
          <div class="carousel-title">{{ img.title }}</div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>

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

      <el-col :xs="24" :sm="8">
        <el-form-item label="标签">
          <el-select v-model="selectedLabels" multiple clearable placeholder="请选择标签">
            <el-option
                v-for="label in labels.items"
                :key="label.id"
                :label="label.title"
                :value="label.title"
            />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :xs="24" :sm="8">
        <el-form-item label="最早存入时间">
          <el-date-picker v-model="minStoreDate"></el-date-picker>
        </el-form-item>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-form-item label="最迟存入时间">
          <el-date-picker v-model="maxStoreDate"></el-date-picker>
        </el-form-item>
      </el-col>
    </el-row>
    <el-button @click="changeSearchType">{{!this.mcpSearch ? '使用mcp搜素' : '取消mcp搜素'}}</el-button>
    <el-form-item label="mcp测试窗口" v-if="this.mcpSearch">
      <el-input v-model="conversation" placeholder="请输入想要的图片描述" />
      <el-button @click="onMcpSearch">搜素</el-button>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click = openAddDialog>添加图片</el-button>
      <el-button type="primary" @click = onAddNewLabel>新建标签</el-button>
      <el-button type="primary" @click = openRemoveLabelDialog>删除标签</el-button>
    </el-form-item>
  </el-form>

  <div class="home-page">
    <h2>我的图片</h2>
    <div class="gallery">
      <ImageCard
        v-for="image in satisfiedImages"
        :key="image.id"
        :item-key="image.id"
        :src="image.path"
        :title="image.title"
        :labels="image.labels"
        height="140"
        @delete ="onDeleteImage(image)"
        @click = "showImageDialog = true; currentImage = image"
      />
    </div>
  </div>

  <el-dialog v-model="showAddDialog" width="720px" append-to-body>
    <AddImageDialog @add="loadAll" @close="showAddDialog = false;" />
  </el-dialog>

  <el-dialog
      v-model="showImageDialog" :close-on-click-modal="false"
      width="800px"
      append-to-body>
    <el-row :gutter="12">
      <el-col :sm="14">
        <img :src="currentImage.path" alt="image" style="max-width:100%;max-height:60vh;display:block;margin:0 auto;" />
      </el-col>
      <el-col :sm="10">
        <el-input v-model="newTitle" :disabled="!this.manageLabels" :placeholder="currentImage.title"/>
        <div>存入时间: {{ currentImage.stored_at || '未知' }}</div>
        <div>分辨率: {{currentImage.width}} * {{currentImage.height}}</div>
        <div>图片类型: {{currentImage.type}}</div>
        <div>修改时间: {{currentImage.modified}}</div>
        <div style="margin-top:12px;">
          <div>标签:</div>
          <div style="margin-top:8px;display:flex;flex-wrap:wrap;gap:8px;">
            <el-button
                v-for="(lab, idx) in (currentImage.labels || [])"
                :key="idx"
                size="mini"
                type="primary"
                plain
                :disabled="!manageLabels"
                @click="showImageDialog = false;  onDeleteImageLabel(lab)"
            >{{ typeof lab === 'string' ? lab : (lab.title || String(lab)) }}
            </el-button>
          </div>
        </div>
        <div>
          <el-button @click="changeManageLabels">{{ this.manageLabels ? '取消管理' : '管理' }}</el-button>
        </div>
        <div>
          <el-text v-if="manageLabels">请选择想要添加的标签</el-text>
          <el-select v-model="newLabels" v-if="this.manageLabels" multiple clearable placeholder="请选择标签">
            <el-option
                v-for="label in nonchosenLabels"
                :key="label.id"
                :label="label.title"
                :value="label.id"
            />
          </el-select>
          <el-button v-if="this.manageLabels" @click="onAddImageLabels">确认修改</el-button>
        </div>
      </el-col>
    </el-row>
  </el-dialog>
  <el-dialog v-model="showRemoveLabelDialog" width="400px" append-to-body title="删除标签">
    <el-form-item label="标签">
      <el-select v-model="removedLabels" multiple clearable placeholder="请选择要删除的标签">
        <el-option
            v-for="label in labels.items"
            :key="label.id"
            :label="label.title"
            :value="label.id"
        />
      </el-select>
    </el-form-item>
    <el-button type="primary" @click="onRemoveLabel">确定</el-button>
  </el-dialog>
</template>

<script>
import {userStore, WebsiteConfig} from '../store/user'
import {images} from "../store/images.js";
import {labels} from "../store/label.js";
import ImageCard from '../components/ImageCard.vue'
import AddImageDialog from '../components/AddImageDialog.vue'
import {ElMessageBox, ElMessage} from "element-plus";

export default {
  name: 'HomePage',
  components: { ImageCard, AddImageDialog },
  data() {
    return{
      showAddDialog: false,
      imageUrl: '',
      imageTitle: '',
      selectedLabels: [],
      newLabels: [],
      showImageDialog: false,
      currentImage: {},
      manageLabels: false,
      deleteImages: false,
      newTitle: '',
      conversation: '',
      mcpSearch:false,
      potentialImageIds: [],
      showRemoveLabelDialog: false,
      removedLabels: [],
      minStoreDate: null,
      maxStoreDate: null
    }
  },
  mounted() {
    this.loadAll();
  },
  methods:{
    openAddDialog() {
      this.showAddDialog = true;
    },
    changeManageLabels() {
      this.manageLabels = !this.manageLabels;
    },
    changeSearchType(){
      this.mcpSearch = !this.mcpSearch;
      if(this.mcpSearch){
        this.imageUrl = '';
        this.imageTitle = '';
        this.selectedLabels = [];
        this.minStoreDate = null;
        this.maxStoreDate = null;
      }else{
        this.conversation = '';
      }
    },
    openRemoveLabelDialog(){
      this.showRemoveLabelDialog = true;
    },
    async onAddImageLabels(){
      if(this.newTitle !== '' || this.newLabels.length > 0){
        const title = this.newTitle || this.currentImage.title;
        const labels = this.newLabels.length > 0 ? JSON.stringify(this.newLabels) : '[]';
        console.log(title + ' ' + labels);
        const url= WebsiteConfig + `/label/addToImage`;
        const res = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            id: this.currentImage.id,
            title: title,
            labels: labels,
          })
        });
        if(!res.ok){
          const text = await res.text().catch(()=>'');
          console.log(text || `请求失败：${res.status}`);
          return;
        }
        await this.loadAll();
        this.showImageDialog = false;
        this.manageLabels = false;
        this.newLabels = [];
        ElMessage({ title: '成功', message: '标签添加成功！', showConfirmButton: true });
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
    },
    async onRemoveLabel(){
      if(this.removedLabels.length === 0){
        await ElMessageBox({ title: '错误', message: '请选择要删除的标签', showConfirmButton: true });
        return;
      }
        const url = WebsiteConfig + '/label/removeFromUser';
        const response = await fetch(url, {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify(this.removedLabels)
        })
        if (!response.ok) {
          const text = await response.text().catch(() => '');
          ElMessageBox({ title: '错误', message: text || `请求失败：${response.status}`, showConfirmButton: true });
          return;
        }
        else{
          ElMessage({ message: '删除标签成功', type: 'success', duration: 2000 });
          await this.loadAll();
          this.removedLabels = [];
          this.showRemoveLabelDialog = false;
        }
    },
    onDeleteImageLabel(label){
      ElMessageBox.confirm(`确认删除标签${label.title}?`,'删除标签', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(async () => {
        try {
          const url = WebsiteConfig + `/label/removeFromImage?p_id=${this.currentImage.id}&l_id=${label.id}`;
          const response = await fetch(url, { method: 'POST' });
          if (!response.ok) {
            const text = await response.text().catch(() => '');
            console.log(text || `请求失败：${response.status}`);
            return;
          }
          ElMessageBox(
              { title: '成功', message: '标签删除成功！', showConfirmButton: true }
          );
          this.showImageDialog = false;
          this.manageLabels = false;
          await this.loadAll();
        } catch (err) {
          console.log('删除标签失败，请稍后重试: ' + err);
        }
      })
    },
    async onDeleteImage(image) {
      ElMessageBox.confirm(`确认删除图片${image.title}?`,'删除图片', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(async () => {
        try {
          const url = WebsiteConfig + `/image/remove?image_id=${image.id}`;
          const response = await fetch(url, { method: 'DELETE' });
          if (!response.ok) {
            const text = await response.text().catch(() => '');
            console.log(text || `请求失败：${response.status}`);
            return;
          }
          ElMessageBox(
              { title: '删除成功', message: '图片删除成功！', showConfirmButton: true }
          );
          await this.loadImages();
        } catch (err) {
          console.log('删除图片失败，请稍后重试: ' + err);
        }
      }).catch(()=>{});
    },
    async onMcpSearch(){
      if(this.conversation.trim() === ''){
        this.potentialImageIds = [];
        return;
      }
      try{
        const url = WebsiteConfig + `/mcp/query?user_id=${userStore.id}&conversation=${encodeURIComponent(this.conversation.trim())}`;
        const response = await fetch(url, {method: 'GET'});
        if(!response.ok){
          const text = await response.text().catch(()=>'');
          console.log(text || `请求失败：${response.status}`);
          return;
        }

        const data = await response.json();
        console.log(data);
        this.potentialImageIds =[];
        data.forEach(item => this.potentialImageIds.push(item.image_id));
        ElMessage({ message: '图片检索完成！', type: 'success' });
      }catch(err){
        console.log('图片检索失败，请稍后重试: '+err);
      }
    },

    async loadImages() {
      try{
        const url = WebsiteConfig + `/image/getImage?user_id=${userStore.id}`;
        const response = await fetch(url,{method: 'GET'})
        if(!response.ok){
          const text = await response.text().catch(()=>'');
          console.log(text || `请求失败：${response.status}`);
          return;
        }
        const data = await response.json();
        images.clear();
        data.forEach(img => images.add({
          id: img.id,
          title: img.title,
          path: userStore.pathToImage + img.path,
          stored_at: img.stored_at,
          labels: img.labels,
          width: img.width,
          height: img.height,
          type: img.type,
          modified: img.modified
        }));
      }catch(err) {
        console.log('获取图片失败，请稍后重试: '+err);
      }
    },

    async loadLabels() {
      try{
        const url = WebsiteConfig + `/label/getLabelsOfUser?user_id=${userStore.id}`;
        const response = await fetch(url,{method: 'GET'})
        if(!response.ok){
          const text = await response.text().catch(()=>'');
          console.log(text || `请求失败：${response.status}`);
          return;
        }
        const data = await response.json();
        labels.clear();
        data.forEach(l => labels.add({id: l.id, title: l.title}));
      }catch(err){
        console.log('获取标签失败，请稍后重试');
      }
    },

    async loadAll() {
      await Promise.all([this.loadImages(), this.loadLabels()]);
    }
  },
  computed: {
    labels() {
      return labels
    },
    carouselImages() {
      return images.items.slice(0, 3);
    },
    satisfiedImages() {
      if(this.mcpSearch){
        return images.items.filter(image => (this.potentialImageIds.length === 0 || this.potentialImageIds.includes(image.id)));
      }
      return images.items.filter(image => {
        const imgPath = image.path || '';
        const imgTitle = image.title || '';
        if (this.imageUrl !== '' && !imgPath.includes(this.imageUrl)) return false;
        if (this.imageTitle !== '' && !imgTitle.includes(this.imageTitle)) return false;
        if (this.minStoreDate !== null && image.stored_at) {
          const imgDate = new Date(image.stored_at);
          if (imgDate < new Date(this.minStoreDate)) return false;
        }
        if (this.maxStoreDate !== null && image.stored_at) {
          const imgDate = new Date(image.stored_at);
          if (imgDate > new Date(this.maxStoreDate)) return false;
        }
        if (this.selectedLabels.length === 0) return true;
        const imageLabelTitles = (image.labels || []).map(l =>
            typeof l === 'string' ? l : (l && (l.title || String(l)))
        );
        return this.selectedLabels.every(sel => imageLabelTitles.includes(sel));
      });
    },
    nonchosenLabels() {
      const chosen = new Set(this.currentImage.labels ?
          this.currentImage.labels.map(l => typeof l === 'string' ? l : (l && (l.title || String(l)))) : []
      );
      return labels.items.filter(l => !chosen.has(l.title));
    }
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
.carousel-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin: 32px auto 10px;
  color: #409EFF;
}

.el-carousel {
  max-width: 750px;
  width: 100%;
  border-radius: 16px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.08);
  background: #fff;
  overflow: hidden;
}

.carousel-image-box {
  position: relative;
  width: 100%;
  height: 330px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  transition: transform 0.4s;
}
.carousel-image-box:hover .carousel-image {
  transform: scale(1.05);
}
.carousel-title {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 0 8px 14px;
  color: #fff;
  font-weight: 500;
  font-size: 22px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.22);
  background: linear-gradient(to top,rgba(0,0,0,0.37),rgba(0,0,0,0.03) 88%);
  border-radius: 0 0 12px 12px;
  pointer-events: none;
}
</style>