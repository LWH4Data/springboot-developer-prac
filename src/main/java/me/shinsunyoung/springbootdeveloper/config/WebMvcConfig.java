package me.shinsunyoung.springbootdeveloper.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // /updates/** 경로로 요청이 오면 uploads/ 디렉터리의 파일을 제공한다.
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/");
  }
}
