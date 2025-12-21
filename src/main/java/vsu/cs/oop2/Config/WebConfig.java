package vsu.cs.oop2.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final FilePathResolver filePathResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String musicPath = filePathResolver.getResolvedMusicPath();
        String imagePath = filePathResolver.getResolvedImagePath();

        registry.addResourceHandler("/Music/**")
                .addResourceLocations("file:" + musicPath + "/");

        registry.addResourceHandler("/Images/**")
                .addResourceLocations("file:" + imagePath + "/")
                .addResourceLocations("classpath:/static/Images");

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/js/**", "/JS/**", "/Js/**")
                .addResourceLocations("classpath:/static/Scripts/")
                .setCachePeriod(0);

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/Styles/")
                .setCachePeriod(0);
    }


}

