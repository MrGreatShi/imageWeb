package org.example.imageweb.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.example.imageweb.dao.ImageDao;
import org.example.imageweb.dao.UserDao;
import org.example.imageweb.entity.EditInfo;
import org.example.imageweb.entity.Image;
import org.example.imageweb.entity.User;
import org.example.imageweb.config.AppConfig;

import org.example.imageweb.entity.ImageExif;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ImageService {
    ImageDao imageDao;
    UserDao userDao;

    public ImageService() {
        this.imageDao = new ImageDao();
        this.userDao = new UserDao();
    }

    public List<Image> getImagesOfUser(int user_id) throws Exception {
        return imageDao.getImagesOfUser(user_id);
    }

    public ImageExif getExif(Image image) throws Exception {
        User user = userDao.getUserByID(image.getUser_id());
        if (user == null) {
            throw new Exception("User not found");
        }
        java.nio.file.Path imagePath = AppConfig.IMAGE_REPOSITORY.resolve(user.getUsername()).resolve(image.getPath().substring(1));
        File imageFile = imagePath.toFile();
        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        int width = 0;
        int height = 0;
        String type = null;
        java.sql.Timestamp modified = new java.sql.Timestamp(imageFile.lastModified());
        try {
            type = java.nio.file.Files.probeContentType(imageFile.toPath());
        } catch (Exception ignored) {}

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                String name = tag.getTagName();
                String desc = tag.getDescription();
                if (name == null || desc == null) continue;
                if ("Image Width".equals(name) || "Width".equals(name)) {
                    String[] parts = desc.split("[^0-9]+");
                    for (String p : parts) {
                        if (!p.isEmpty()) { width = Integer.parseInt(p); break; }
                    }
                } else if ("Image Height".equals(name) || "Height".equals(name)) {
                    String[] parts = desc.split("[^0-9]+");
                    for (String p : parts) {
                        if (!p.isEmpty()) { height = Integer.parseInt(p); break; }
                    }
                } else if (type == null && ("Detected File Type Name".equals(name) || "File Type".equals(name) || "MIME Type".equals(name))) {
                    type = desc;
                }
            }
        }

        ImageExif exif = new ImageExif(width, height, type, modified);
        //System.out.println("EXIF: width=" + width + ", height=" + height + ", type=" + type + ", modified=" + modified);
        return exif;
    }

    public int getLatestImageId(int user_id) throws Exception {
        List<Image> images = imageDao.getImagesOfUser(user_id);
        int latestImageId = 0;
        for (Image image : images) {
            if(image.getId() > latestImageId) {
                latestImageId = image.getId();
            }
        }
        return latestImageId;
    }
    public void storeImage(int user_id, String title, MultipartFile srcFile) throws Exception {

        User user = userDao.getUserByID(user_id);
        if (user == null) {
            throw new Exception("User not found");
        }

        java.nio.file.Path destDir = AppConfig.IMAGE_REPOSITORY.resolve(user.getUsername());

        try {
            String srcName = srcFile.getOriginalFilename();
            String ext = "";
            int idx = srcName.lastIndexOf('.');
            if (idx >= 0) {
                ext = srcName.substring(idx); // 包含点号
            }

            String baseName = title;
            if (!title.endsWith(ext)) {
                baseName = title;
            }
            String filename = baseName;
            if (!filename.endsWith(ext)) {
                filename = filename + ext;
            }

            Path destFile = destDir.resolve(filename);
            // 若已存在则生成唯一文件名（添加计数）
            int counter = 1;
            while (Files.exists(destFile)) {
                String candidateBase = baseName + "_" + counter;
                String candidate = candidateBase + (ext.isEmpty() ? "" : ext);
                destFile = destDir.resolve(candidate);
                filename = candidate;
                counter++;
            }
            //System.out.println("Storing image " + filename + " to " + destFile);
            java.nio.file.Files.copy(srcFile.getInputStream(), destFile);
            File imageFile = destFile.toFile();

            // 将相对路径存入数据库（例如 username/filename）
            String storedPath = "/" + filename;
            Image image = new Image(0, user_id, title, storedPath);
            imageDao.storeImage(image);
        } catch (java.io.IOException e) {
            throw new Exception("Failed to store image file", e);
        }
    }

    public void removeImage(int id) throws Exception {
        Image image = imageDao.getImageByID(id);
        if (image == null) {
            throw new Exception("Image not found");
        }
        User user = userDao.getUserByID(image.getUser_id());
        if (user == null) {
            throw new Exception("User not found");
        }
        String username = user.getUsername();
        // 删除文件系统中的图片文件
        java.nio.file.Path userDirPath = AppConfig.IMAGE_REPOSITORY.resolve(username);
        java.nio.file.Path imagePath = userDirPath.resolve(image.getPath().substring(1));
        try {
            if (java.nio.file.Files.exists(imagePath)) {
                java.nio.file.Files.delete(imagePath);
                //System.out.println("Deleted image " + imagePath + " succeed");
            }
            else{
                throw new Exception("Image file not found");
            }
            // 删除数据库中的记录
            imageDao.removeImage(id);
        } catch (java.io.IOException e) {
            throw new Exception("Failed to delete image file", e);
        }
    }

    public void modifyImageName(int id, String title) throws Exception {
        imageDao.modifyImageName(id, title);
    }

    public void editImage(EditInfo editInfo) throws Exception {
        Image image = imageDao.getImageByID(editInfo.image_id);
        User user = userDao.getUserByID(image.getUser_id());
        if (user == null) {
            throw new Exception("User not found");
        }
        java.nio.file.Path imagePath = AppConfig.IMAGE_REPOSITORY.resolve(user.getUsername()).resolve(image.getPath().substring(1));
        System.out.println(imagePath);
        File imageFile = imagePath.toFile();
        if(!imageFile.exists()){
            throw new Exception("Image file not found");
        }
        // 读取原图
        BufferedImage srcImg = ImageIO.read(imageFile);
        if(srcImg == null){
            throw new Exception("Image not found");
        }

        // 先旋转整张原图（如果角度不为 0）
        int rotation = ((editInfo.rotation % 360) + 360) % 360;
        BufferedImage rotatedSrc = srcImg;
        if (rotation != 0) {
            double theta = Math.toRadians(rotation);
            int w = srcImg.getWidth();
            int h = srcImg.getHeight();
            double sin = Math.abs(Math.sin(theta));
            double cos = Math.abs(Math.cos(theta));
            int newW = (int) Math.floor(w * cos + h * sin);
            int newH = (int) Math.floor(h * cos + w * sin);
            BufferedImage tmp = new BufferedImage(newW, newH, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D rg = tmp.createGraphics();
            rg.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
            at.translate(newW / 2.0, newH / 2.0);
            at.rotate(theta);
            at.translate(-w / 2.0, -h / 2.0);
            rg.drawImage(srcImg, at, null);
            rg.dispose();
            rotatedSrc = tmp;
        }

        // 在旋转后的图上保证并修正裁剪参数（传入应为自然像素，且前端在旋转后计算的坐标）
        int sx = Math.max(0, Math.min(editInfo.x, rotatedSrc.getWidth() - 1));
        int sy = Math.max(0, Math.min(editInfo.y, rotatedSrc.getHeight() - 1));
        int sw = Math.max(1, Math.min(editInfo.width, rotatedSrc.getWidth() - sx));
        int sh = Math.max(1, Math.min(editInfo.height, rotatedSrc.getHeight() - sy));

        // 裁剪并复制为独立 BufferedImage（避免 getSubimage 视图问题）
        BufferedImage cropped = rotatedSrc.getSubimage(sx, sy, sw, sh);
        BufferedImage working = new java.awt.image.BufferedImage(cropped.getWidth(), cropped.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = working.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(cropped, 0, 0, null);
        g2.dispose();
        // 色相/亮度/对比度调整（逐像素 HSB 处理，近似）
        float hueDeg = editInfo.hue; // 负数或正数均可
        float hueOffset = (hueDeg / 360f); // 加到 H 分量上
        float brightnessFactor = Math.max(0f, editInfo.brightness / 100f); // 1.0 = 原始
        float contrastFactor = Math.max(0f, editInfo.contrast / 100f); // 1.0 = 原始

        int width = working.getWidth();
        int height = working.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = working.getRGB(x, y);
                int alpha = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);
                float h = hsb[0] + hueOffset;
                h = h - (float) Math.floor(h); // 保持在 [0,1)
                float s = hsb[1];
                float bright = hsb[2];

                // 应用亮度缩放
                bright = bright * brightnessFactor;
                // 应用对比度（以 brightness 为基准的近似方法）
                bright = ((bright - 0.5f) * contrastFactor) + 0.5f;

                // 限制范围
                if (bright < 0f) bright = 0f;
                if (bright > 1f) bright = 1f;

                int rgbNew = java.awt.Color.HSBtoRGB(h, s, bright);
                // HSBtoRGB 返回不含 alpha 的 RGB（24-bit），高位包含 alpha=255
                int newR = (rgbNew >> 16) & 0xff;
                int newG = (rgbNew >> 8) & 0xff;
                int newB = rgbNew & 0xff;
                int newArgb = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
                working.setRGB(x, y, newArgb);
            }
        }
        // 保存结果
        if(editInfo.IsReplace){
            java.nio.file.Path outputPath = imagePath;
            String formatName = "png";
            try {
                javax.imageio.ImageIO.write(working, formatName, outputPath.toFile());
            } catch (java.io.IOException e) {
                throw new Exception("Failed to save edited image", e);
            }
        }else{
            java.nio.file.Path outputPath = imagePath;
            String formatName = "png";
            try {
                // 生成新文件名
                String filename = image.getTitle();
                String pathStr = image.getPath();
                String baseName = filename;
                String ext = "";
                int idx = pathStr.lastIndexOf('.');
                if (idx >= 0) {
                    ext = pathStr.substring(idx); // 包含点号
                }
                String newFilename = baseName + "_edited";
                java.nio.file.Path newImagePath = outputPath.getParent().resolve(newFilename + ext);
                int counter = 1;
                while (Files.exists(newImagePath)) {
                    newFilename = baseName + "_edited_" + counter;
                    newImagePath = outputPath.getParent().resolve(newFilename + ext);
                    counter++;
                }
                javax.imageio.ImageIO.write(working, formatName, newImagePath.toFile());
                // 存入数据库
                String storedPath = "/" + newFilename + ext;
                Image newImage = new Image(0, user.getId(), newFilename, storedPath);
                imageDao.storeImage(newImage);
            } catch (java.io.IOException e) {
                throw new Exception("Failed to save edited image", e);
            }
        }
    }
}
