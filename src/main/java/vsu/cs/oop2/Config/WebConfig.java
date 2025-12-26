package vsu.cs.oop2.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * КОНФИГУРАЦИЯ ДЛЯ СТАТИЧЕСКИХ РЕСУРСОВ
 *
 * Настраивает маппинг URL на физические директории и ресурсы classpath.
 *
 * Основные настройки:
 * - /Music/** → файловая система (аудиофайлы)
 * - /Images/** → файловая система + classpath (изображения)
 * - /static/** → classpath (общие ресурсы)
 * - /js/** → classpath (JavaScript)
 * - /css/** → classpath (стили CSS)
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final FilePathResolver filePathResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Аудиофайлы из файловой системы
        registry.addResourceHandler("/Music/**")
                .addResourceLocations("file:" + filePathResolver.getMusicUploadPath() + "/");

        // Изображения: сначала файловая система, потом classpath для ico
        registry.addResourceHandler("/Images/**")
                .addResourceLocations("file:" + filePathResolver.getImageUploadPath() + "/")
                .addResourceLocations("classpath:/static/Images");

        // Общие статические ресурсы
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // JavaScript файлы
        registry.addResourceHandler("/js/**", "/JS/**", "/Js/**")
                .addResourceLocations("classpath:/static/Scripts/");

        // CSS файлы (без кэширования для разработки)
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/Styles/");
    }
}

