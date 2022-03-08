package com.anthill.ofministatisticsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://myceramic-pro.herokuapp.com", "http://myceramic-app",
                        "https://myceramic.pro", "https://office.ceramic-pro.com",
                        "https://test-ceramic.herokuapp.com",
                        "https://www.ceramic-pro.app", "https://ceramic-pro.app")
                .allowCredentials(true)
                .allowedMethods("POST", "GET", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
