import { reactive } from 'vue'

export const images = reactive({
  items: [
      {id:1,
        title:'image1',
        path:'image1.jpg',
        labels:[{id:1, title:'label1'}]
      }
  ],
  add(picture) {
    if(!picture) return;
    this.items.push(picture);
  },
  clear(){
    this.items.splice(0,this.items.length);
  }
})