package es.codeurjc.global_mart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir solicitudes bajo /new/ a index.html para manejar rutas de Angular
        registry.addViewController("/new/").setViewName("forward:/new/index.html");
        registry.addViewController("/new/{spring:[^\\.]*}").setViewName("forward:/new/index.html");
        // registry.addViewController("/new/**/{spring:[^\\.]*}").setViewName("forward:/new/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/new/**")
                .addResourceLocations("classpath:/static/new/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/api/**")
                .allowedOrigins("https://localhost:8443")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}