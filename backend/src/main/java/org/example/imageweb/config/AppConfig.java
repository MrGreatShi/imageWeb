package org.example.imageweb.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig {
    // 项目中的资源图片库根目录（相对于项目根目录）
    public static final Path IMAGE_REPOSITORY = Paths.get("backend", "src", "main", "resources", "imageRepository");
    public static final String DBurl = "jdbc:mysql://localhost:3306/imageWeb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    public static final String DBuser = "root";
    public static final String DBpassword = "Mysql19491001";

    private AppConfig() { }
}
