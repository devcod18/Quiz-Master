package com.example.quizmaster.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Quiz Master API",
                version = "v1",
                description = "API for managing quizzes, users, and admin activities in the QuizMaster application.",
                contact = @Contact(name = "QuizMaster Support", url = "http://quizmaster.example.com",
                        email = "support@quizmaster.example.com"),
                license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")
        ),
        security = @SecurityRequirement(name = "Bearer")
)
@SecurityScheme(
        name = "Bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
