package com.anthill.ofministatisticsapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OfMiniStatisticsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfMiniStatisticsApiApplication.class, args);
	}

	@Bean
	public OpenAPI openApiConfig() {
		return new OpenAPI().info(apiInfo());
	}

	public Info apiInfo() {
		Info info = new Info();
		info
				.title("OFMiniStatistics REST API")
				.description("REST API for OFMiniStatistics app project")
				.version("v1.0.0");
		return info;
	}
}
