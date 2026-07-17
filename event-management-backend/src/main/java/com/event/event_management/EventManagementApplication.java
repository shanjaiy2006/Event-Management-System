package com.event.event_management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
        security = {
                @SecurityRequirement(name = "BearerAuth")
        }
)
@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableScheduling
public class EventManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventManagementApplication.class, args);
	}

}
