package com.example.SmartEcommercePlatform.Config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Smart E-commerce API",
                description = "Enterprise-grade backend for an e-commerce platform.",
                version = "1.0",
                contact = @Contact(name = "Lead Architect Nisa")
        ),
        security = {
                @SecurityRequirement(name = "Bearer Authentication") // Applies this globally to all endpoints
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "Enter your JWT token here to authenticate your API calls.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
