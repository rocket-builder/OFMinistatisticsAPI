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
                .allowedOrigins("https://of-mini-statistic-app-dev.herokuapp.com")
                .allowCredentials(true)
                .allowedMethods("POST", "GET", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
