package com.hyun.udong.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        ArrayList<String> allowedOriginPatterns = new ArrayList<>();
        allowedOriginPatterns.add("http://localhost:5173");
        allowedOriginPatterns.add("http://localhost:3000");

        String[] patterns = allowedOriginPatterns.toArray(String[]::new);
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOriginPatterns(patterns)
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true);
    }
}
