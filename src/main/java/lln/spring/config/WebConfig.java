package lln.spring.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${uploadImagesDir}")
    private String uploadImagesDir;
    
    @Value("${uploadAvatarsDir}")
    private String uploadAvatarsDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加外部上传目录，同时添加项目内静态资源目录作为后备选项
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:"+uploadImagesDir, "classpath:/static/images/");
        
        registry.addResourceHandler("/api/images/avatars/**")
                .addResourceLocations("file:"+uploadAvatarsDir);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
// 设置格式化内容
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}