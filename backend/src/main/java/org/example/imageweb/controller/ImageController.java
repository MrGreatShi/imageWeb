package org.example.imageweb.controller;

import org.example.imageweb.config.AppConfig;
import org.example.imageweb.entity.EditInfo;
import org.example.imageweb.entity.Image;
import org.example.imageweb.entity.ImageExif;
import org.example.imageweb.entity.Label;
import org.example.imageweb.service.ImageService;
import org.example.imageweb.service.LabelService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/image")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private LabelService labelService;

    public static class ImageInfo{
        public int id;
        public String title;
        public String path;
        public Timestamp stored_at;
        public List<Label> labels;
        public int width;
        public int height;
        public String type;

        public Timestamp modified;
    }
    @GetMapping("/getImage")
    public ResponseEntity<?> getImages(@RequestParam int user_id) throws Exception {
        try{
            List<Image> images = imageService.getImagesOfUser(user_id);
            List<ImageInfo> imageInfos = new ArrayList<>();
            for (Image image : images) {
                int id = image.getId();
                List<Label> labels = labelService.getLabelsOfImage(id);
                ImageExif exif = imageService.getExif(image);
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.id = id;
                imageInfo.title = image.getTitle();
                imageInfo.path = image.getPath();
                imageInfo.stored_at = image.getStored_at();
                imageInfo.labels = labels;
                imageInfo.width = exif.getWidth();
                imageInfo.height = exif.getHeight();
                imageInfo.type = exif.getType();
                imageInfo.modified = exif.getModified();
                imageInfos.add(imageInfo);
            }
            return ResponseEntity.ok(imageInfos);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "/store")
    public ResponseEntity<?> storeImage(
            @RequestParam("user_id") int user_id,
            @RequestParam("title") String title,
            @RequestParam(value = "labels", required = false) String labels,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            imageService.storeImage(user_id, title, file);
            int image_id = imageService.getLatestImageId(user_id);
            // 处理 labels 字符串为 List<Label>
            if(!labels.isEmpty() && !labels.equals("[]")){
                int size = labels.length();
                String[] labelsList = labels.substring(1,size-1).split(",");
                for(String l_id_str : labelsList){
                    int id = Integer.parseInt(l_id_str);
                    labelService.addLabelToImage(image_id, id);
                }
            }
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("保存图片失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeImage(@RequestParam int image_id) throws Exception {
        try{
            System.out.println("try to delete image " + image_id);
            imageService.removeImage(image_id);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modifyName")
    public ResponseEntity<?> modifyImage(@RequestParam int image_id,
                               @RequestParam String title) throws Exception {
        try{
            imageService.modifyImageName(image_id, title);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editImage(@RequestBody EditInfo editInfo) throws Exception{
        try{
            System.out.println("try to edit image with info: " + new ObjectMapper().writeValueAsString(editInfo));
            imageService.editImage(editInfo);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
