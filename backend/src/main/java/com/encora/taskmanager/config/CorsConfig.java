package com.encora.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final String commonOrigin = "http://localhost:3000";
        registry.addMapping("/api/auth/**")
                .allowedOrigins(commonOrigin)
                .allowedMethods("POST");
        registry.addMapping("/api/tasks/**")
                .allowedOrigins(commonOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
