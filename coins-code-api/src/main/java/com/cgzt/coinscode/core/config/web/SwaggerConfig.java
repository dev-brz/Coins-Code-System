package com.cgzt.coinscode.core.config.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(security = @SecurityRequirement(name = "basicAuth"))
@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "basic", name = "basicAuth")
class SwaggerConfig {
    static {
        SpringDocUtils.getConfig().replaceWithClass(char[].class, String.class);
    }
}