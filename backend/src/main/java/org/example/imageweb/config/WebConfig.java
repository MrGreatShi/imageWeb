// java
package org.example.imageweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 若配置不存在，使用空字符串作为默认值，后续进行显式校验
    @Value("${image.repository.path:}")
    private String imageRepositoryPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = imageRepositoryPath == null ? "" : imageRepositoryPath.trim();

        if (!StringUtils.hasText(path)) {
            throw new IllegalStateException("配置项 `image.repository.path` 未设置。请在 backend/src/main/resources/application.properties 中添加，例如：\nimage.repository.path=C:/images/");
        }

        // 统一使用正斜杠并保证以 / 结尾
        String normalized = path.replace('\\', '/');
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }

        // 注册静态资源映射
        registry.addResourceHandler("/imageRepository/**")
                .addResourceLocations("file:" + normalized);
    }
}