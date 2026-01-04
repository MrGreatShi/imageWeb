<template>
  <div class="edit-page">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="16">
        <div class="canvas-area">
          <div class="crop-area" :style="{position:'relative'}" @mousedown.stop>
            <img ref="previewImage" :src="currentImage.path" class="preview-img" :style="previewStyle" @load="onImageLoad" />
            <div
                class="crop-box"
                :style="computedCropBoxStyle"
                @mousedown.prevent="startDrag"
            >
              <div class="handle br" @mousedown.stop.prevent="startResize($event, 'br')"></div>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="8">
        <el-card class="control-card" shadow="hover">
          <span class="hint">请勿裁剪过于边缘的区域，可能出现裁剪异常</span>
          <div class="control-group">
            <div class="control-row">
            </div>

            <div class="control-row">
              <el-button @click="rotate(-90)">左转 90°</el-button>
              <el-button @click="rotate(90)">右转 90°</el-button>
            </div>

            <div class="control-row">
              <div class="label">色相调整</div>
              <el-slider v-model="hue" :min="-180" :max="180" show-input @change="onEditChange" />
            </div>

            <div class="control-row">
              <div class="label">亮度</div>
              <el-slider v-model="brightness" :min="0" :max="200" show-input @change="onEditChange" />
            </div>

            <div class="control-row">
              <div class="label">对比度</div>
              <el-slider v-model="contrast" :min="0" :max="200" show-input @change="onEditChange" />
            </div>

            <div class="control-row">
              <el-button @click="reset">重置</el-button>
            </div>

            <div class="control-row">
              <el-button @click="openChooseDialog">更换图片</el-button>
              <input type="file" ref="fileInput" @change="onFileChange" style="display: none;" />
            </div>
            <div class="control-row">
              <div>保存方式</div>
              <el-button @click="onChangeSaveMethod">{{IsReplace ? '覆盖原文件' : '新建文件'}}</el-button>
              <input type="file" ref="fileInput" @change="onFileChange" style="display: none;" />
            </div>
            <div class="spacer" />

          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-button type="success" class="save-btn" @click="onSave">保存</el-button>
  </div>

  <el-dialog v-model="showChangeDialog" width="720px" append-to-body>
    <el-form-item label="图片地址">
      <el-input v-model="imageUrl" placeholder="请输入图片地址" />
    </el-form-item>
    <el-form-item label="图片标题">
      <el-input v-model="imageTitle" placeholder="请输入图片标题" />
    </el-form-item>
    <el-form-item label="标签">
      <el-select v-model="selectedLabels" multiple clearable placeholder="请选择标签">
        <el-option
            v-for="label in labels.items || []"
            :key="label.id"
            :label="label.title"
            :value="label.title"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="选择图片">
      <el-select v-model="currentImage" clearable placeholder="请选择图片">
        <el-option
            v-for="image in satisfiedImages"
            :key="image.id"
            :label="image.title"
            :value="image"
            @click="onChooseImage(image)"
        />
      </el-select>
    </el-form-item>
  </el-dialog>
</template>

<script>
import {images} from "../store/images.js";
import {labels} from "../store/label.js";
import {userStore, WebsiteConfig} from "../store/user.js";

export default {
  name: 'EditImagePage',
  data(){
    return{
      currentImage: images.items[0],
      showChangeDialog: false,
      imageUrl: '',
      imageTitle: '',
      selectedLabels: [],
      cropRect: { x: 0, y: 0, w: 100, h: 100 },
      dragging: false,
      resizing: false,
      resizeHandle: null,
      dragStart: { x: 0, y: 0, sx: 0, sy: 0 },
      previewStyle: {},
      hue: 0,
      brightness: 100,
      contrast: 100,
      rotation: 0,
      IsReplace: false
    }
  },
  mounted() {
    this.loadAll();
    window.addEventListener('mousemove', this.onPointerMove);
    window.addEventListener('mouseup', this.onPointerUp);
  },
  methods:{
    openChooseDialog() {
      this.showChangeDialog = true;
    },
    onChooseImage(image) {
      this.currentImage = image;
      this.showChangeDialog = false;
    },
    onChangeSaveMethod() {
      this.IsReplace = !this.IsReplace;
    },
    onImageLoad() {
      this.initCropBox();
    },
    initCropBox() {
      this.$nextTick(() => {
        const img = this.$refs.previewImage;
        if (!img) return;
        const parentRect = img.parentElement.getBoundingClientRect();
        const rect = img.getBoundingClientRect();
        const imgLeft = rect.left - parentRect.left;
        const imgTop = rect.top - parentRect.top;

        const w = Math.min(300, rect.width * 0.6);
        const h = Math.min(300, rect.height * 0.6);
        this.cropRect.w = Math.round(w);
        this.cropRect.h = Math.round(h);
        this.cropRect.x = Math.round(imgLeft + (rect.width - w) / 2);
        this.cropRect.y = Math.round(imgTop + (rect.height - h) / 2);
      });
    },
    startDrag(e) {
      this.dragging = true;
      this.dragStart.x = e.clientX;
      this.dragStart.y = e.clientY;
      this.dragStart.sx = this.cropRect.x;
      this.dragStart.sy = this.cropRect.y;
    },
    startResize(e, handle) {
      this.resizing = true;
      this.resizeHandle = handle;
      this.dragStart.x = e.clientX;
      this.dragStart.y = e.clientY;
      this.dragStart.sx = this.cropRect.w;
      this.dragStart.sy = this.cropRect.h;
    },
    onPointerMove(e) {
      const img = this.$refs.previewImage;
      if (!img) return;
      const parentRect = img.parentElement.getBoundingClientRect();
      const rect = img.getBoundingClientRect();
      const imgLeft = rect.left - parentRect.left;
      const imgTop = rect.top - parentRect.top;
      const imgW = rect.width;
      const imgH = rect.height;

      if (this.dragging) {
        const dx = e.clientX - this.dragStart.x;
        const dy = e.clientY - this.dragStart.y;
        let nx = this.dragStart.sx + dx;
        let ny = this.dragStart.sy + dy;
        // 边界：相对于父容器，限制在图片区域内
        nx = Math.max(imgLeft, Math.min(nx, imgLeft + imgW - this.cropRect.w));
        ny = Math.max(imgTop, Math.min(ny, imgTop + imgH - this.cropRect.h));
        this.cropRect.x = nx;
        this.cropRect.y = ny;
      } else if (this.resizing) {
        const dx = e.clientX - this.dragStart.x;
        const dy = e.clientY - this.dragStart.y;
        let nw = this.dragStart.sx + dx;
        let nh = this.dragStart.sy + dy;
        // 最大尺寸不能超过图片右/下边界
        const maxW = Math.max(0, imgLeft + imgW - this.cropRect.x);
        const maxH = Math.max(0, imgTop + imgH - this.cropRect.y);
        nw = Math.max(20, Math.min(nw, maxW));
        nh = Math.max(20, Math.min(nh, maxH));
        this.cropRect.w = nw;
        this.cropRect.h = nh;
      }
    },
    rotate(deg) {
      const img = this.$refs.previewImage;
      // 如果图片尚未就绪，只更新角度和样式
      if (!img) {
        this.rotation = (this.rotation + deg) % 360;
        if (this.rotation < 0) this.rotation += 360;
        this.previewStyle.transform = `rotate(${this.rotation}deg)`;
        return;
      }

      // 记录旧状态（包含图片在父容器内的偏移）
      const parentRect = img.parentElement.getBoundingClientRect();
      const oldRect = img.getBoundingClientRect();
      const oldImgLeft = oldRect.left - parentRect.left;
      const oldImgTop = oldRect.top - parentRect.top;
      const oldW = oldRect.width || 1;
      const oldH = oldRect.height || 1;

      // 以图片显示区为基准计算裁剪框的相对比例（相对于图片左上角）
      const rx = (this.cropRect.x - oldImgLeft) / oldW;
      const ry = (this.cropRect.y - oldImgTop) / oldH;
      const rw = this.cropRect.w / oldW;
      const rh = this.cropRect.h / oldH;

      // 更新角度并设置样式（触发 DOM 变更）
      this.rotation = (this.rotation + deg) % 360;
      if (this.rotation < 0) this.rotation += 360;
      this.previewStyle.transform = `rotate(${this.rotation}deg)`;

      // 等待 DOM 更新后根据新视口调整裁剪框
      this.$nextTick(() => {
        const newRect = img.getBoundingClientRect();
        const newImgLeft = newRect.left - parentRect.left;
        const newImgTop = newRect.top - parentRect.top;
        const newW = newRect.width || 1;
        const newH = newRect.height || 1;

        const oldOrientSwapped = (oldW > oldH);
        const newOrientSwapped = (newW > newH);

        // 判断是否宽高方向互换（以显示尺寸为准）
        if ((oldW !== newW && oldH !== newH) && ( (oldW/oldH) !== (newW/newH) )) {
          // 保守处理：如果宽高方向互换（90/270），将 x/y 与 w/h 对调（按比例）
          const oldRotationNorm = ((this.rotation - deg) % 360 + 360) % 360;
          const newRotationNorm = ((this.rotation) % 360 + 360) % 360;
          const swapped = ((oldRotationNorm % 180) !== 0) !== ((newRotationNorm % 180) !== 0);
          if (swapped) {
            this.cropRect.x = Math.round(newImgLeft + ry * newW);
            this.cropRect.y = Math.round(newImgTop + rx * newH);
            this.cropRect.w = Math.round(rh * newW);
            this.cropRect.h = Math.round(rw * newH);
          } else {
            this.cropRect.x = Math.round(newImgLeft + rx * newW);
            this.cropRect.y = Math.round(newImgTop + ry * newH);
            this.cropRect.w = Math.round(rw * newW);
            this.cropRect.h = Math.round(rh * newH);
          }
        } else {
          // 常规缩放/平移，仅按新视口缩放位置/大小（基于图片左上角）
          this.cropRect.x = Math.round(newImgLeft + rx * newW);
          this.cropRect.y = Math.round(newImgTop + ry * newH);
          this.cropRect.w = Math.round(rw * newW);
          this.cropRect.h = Math.round(rh * newH);
        }

        // 边界和最小尺寸约束（最小 20px）
        const minSize = 20;
        this.cropRect.w = Math.max(minSize, Math.min(this.cropRect.w, newW));
        this.cropRect.h = Math.max(minSize, Math.min(this.cropRect.h, newH));
        this.cropRect.x = Math.max(newImgLeft, Math.min(this.cropRect.x, newImgLeft + newW - this.cropRect.w));
        this.cropRect.y = Math.max(newImgTop, Math.min(this.cropRect.y, newImgTop + newH - this.cropRect.h));
      });
    },
    onPointerUp() {
      this.dragging = false;
      this.resizing = false;
      this.resizeHandle = null;
    },
    applyFilter() {
      this.previewStyle.filter = `hue-rotate(${this.hue}deg) brightness(${this.brightness}%) contrast(${this.contrast}%)`;
    },
    onEditChange() {
      this.applyFilter();
    },
    reset() {
      this.hue = 0;
      this.brightness = 100;
      this.contrast = 100;
      this.rotation = 0;
      this.previewStyle = {};
      this.initCropBox();
    },
    // javascript
// 替换文件：`frontend/src/pages/editImage.vue` 中 methods 的 onSave
    async onSave() {
      const img = this.$refs.previewImage;
      if (!img || !img.complete || img.naturalWidth === 0) {
        this.$message.error('图片尚未加载，无法提交');
        return;
      }

      // 原始像素尺寸
      const naturalW = img.naturalWidth;
      const naturalH = img.naturalHeight;
      const rect = img.getBoundingClientRect();
      const parentRect = img.parentElement.getBoundingClientRect();

      // 图片在父容器内的左/上偏移（cropRect 是相对于父容器的）
      const imgLeft = rect.left - parentRect.left;
      const imgTop = rect.top - parentRect.top;

      // 当前旋转（规范化到 0-359）
      const rot = ((this.rotation % 360) + 360) % 360;
      // 若为 90/270，需要把 natural 宽高互换（前端以 CSS 旋转显示）
      const rotatedNaturalW = (rot % 180 === 0) ? naturalW : naturalH;
      const rotatedNaturalH = (rot % 180 === 0) ? naturalH : naturalW;

      // 显示区域的宽高（已经受 CSS transform 影响）
      const dispW = rect.width || 1;
      const dispH = rect.height || 1;

      // 将裁剪框从父容器像素 -> 旋转后自然像素
      const scaleX = rotatedNaturalW / dispW;
      const scaleY = rotatedNaturalH / dispH;

      // 相对于图片左上角的像素（注意减去 imgLeft/imgTop）
      let sx = Math.round((this.cropRect.x - imgLeft) * scaleX);
      let sy = Math.round((this.cropRect.y - imgTop) * scaleY);
      let sw = Math.max(1, Math.round(this.cropRect.w * scaleX));
      let sh = Math.max(1, Math.round(this.cropRect.h * scaleY));

      // 边界约束到旋转后的自然尺寸
      sx = Math.max(0, Math.min(sx, rotatedNaturalW - 1));
      sy = Math.max(0, Math.min(sy, rotatedNaturalH - 1));
      sw = Math.max(1, Math.min(sw, rotatedNaturalW - sx));
      sh = Math.max(1, Math.min(sh, rotatedNaturalH - sy));

      const payload = {
        image_id: this.currentImage && this.currentImage.id ? this.currentImage.id : null,
        x: sx,
        y: sy,
        width: sw,
        height: sh,
        hue: this.hue,
        brightness: this.brightness,
        contrast: this.contrast,
        rotation: rot,
        IsReplace: this.IsReplace
      };
      console.log(payload);
      try {
        const url = WebsiteConfig + '/image/edit';
        const resp = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        if (resp.ok) {
          await this.loadAll();
          this.$message.success('修改成功');
        } else {
          const txt = await resp.text().catch(()=> '');
          this.$message.error('提交失败: ' + (txt || resp.status));
        }
      } catch (e) {
        console.error(e);
        this.$message.error('提交时发生错误');
      }
    },

    async loadImages() {
      try{
        const url = WebsiteConfig + `/image/getImage?user_id=${userStore.id}`;
        const response = await fetch(url,{method: 'GET'})
        if(!response.ok){
          const text = await response.text().catch(()=> '');
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
          modified: img.modified,
          edits: img.edits || null
        }));
        this.$nextTick(() => { if (this.currentImage) this.initCropBox(); });
      }catch(err) {
        console.log('获取图片失败，请稍后重试: '+err);
      }
    },

    async loadLabels() {
      try{
        const url = WebsiteConfig + `/label/getLabelsOfUser?user_id=${userStore.id}`;
        const response = await fetch(url,{method: 'GET'})
        if(!response.ok){
          const text = await response.text().catch(()=> '');
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
    },

    onFileChange(e) {
      const f = e.target.files && e.target.files[0];
      if (!f) return;
      const reader = new FileReader();
      reader.onload = (ev) => {
        if (this.currentImage) {
          this.currentImage.path = ev.target.result;
          this.$nextTick(() => this.initCropBox());
        }
      };
      reader.readAsDataURL(f);
    }
  },
  computed:{
    labels() { return labels },
    satisfiedImages() {
      return images.items.filter(image => {
        const imgPath = image.path || '';
        const imgTitle = image.title || '';
        if (this.imageUrl !== '' && !imgPath.includes(this.imageUrl)) return false;
        if (this.imageTitle !== '' && !imgTitle.includes(this.imageTitle)) return false;
        if (this.selectedLabels.length === 0) return true;
        const imageLabelTitles = (image.labels || []).map(l => typeof l === 'string' ? l : (l && (l.title || String(l))));
        return this.selectedLabels.every(sel => imageLabelTitles.includes(sel));
      });
    },
    computedCropBoxStyle() {
      return {
        position: 'absolute',
        left: this.cropRect.x + 'px',
        top: this.cropRect.y + 'px',
        width: this.cropRect.w + 'px',
        height: this.cropRect.h + 'px',
        border: '2px dashed #409EFF',
        boxSizing: 'border-box',
        cursor: this.dragging ? 'grabbing' : 'grab',
        background: 'rgba(0,0,0,0.15)'
      };
    }
  },
  beforeUnmount() {
    window.removeEventListener('mousemove', this.onPointerMove);
    window.removeEventListener('mouseup', this.onPointerUp);
  }
}
</script>

<style scoped>
.edit-page { padding: 16px; }
.canvas-area { background: #fafafa; min-height: 480px; display: flex; align-items: center; justify-content: center; border-radius: 6px; border: 1px solid rgba(0,0,0,0.04); position: relative; }
.preview-img { max-width: 100%; max-height: 100%; display: block; transition: transform 0.2s ease, filter 0.2s ease; user-select: none; -webkit-user-drag: none; }
.control-card { height: 100%; }
.control-group { display: flex; flex-direction: column; gap: 12px; }
.control-row { display: flex; gap: 8px; align-items: center; }
.label { width: 60px; color: #666; font-size: 13px; }
.note { font-size: 12px; color: #999; }
.spacer { flex: 1; }
.save-btn { position: fixed; right: 20px; bottom: 20px; }
.crop-box { position: absolute; box-sizing: border-box; }
.crop-box .handle { position: absolute; width: 12px; height: 12px; background: #fff; border: 1px solid #409EFF; box-sizing: border-box; }
.crop-box .handle.br { right: -7px; bottom: -7px; cursor: se-resize; border-radius: 2px; }
</style>