package com.isma.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata exposed through the swagger-ui and api-docs endpoints.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("1.0.0")
                        .description("REST API to manage projects and tasks; errors follow RFC 7807")
                        .contact(new Contact()
                                .name("isma123nassif")
                                .url("https://github.com/isma123nassif"))
                        .license(new License().name("MIT")));
    }
}
