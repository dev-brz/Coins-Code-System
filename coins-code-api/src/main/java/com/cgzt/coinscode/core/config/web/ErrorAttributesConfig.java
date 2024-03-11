package com.cgzt.coinscode.core.config.web;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

@Configuration
class ErrorAttributesConfig {
    @Bean
    ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

                if (errorAttributes.containsKey("errors") && errorAttributes.get("errors") instanceof List<?> errors) {
                    List<String> simplifiedErrors = errors.stream()
                            .filter(FieldError.class::isInstance)
                            .map(FieldError.class::cast)
                            .map(error -> "%s %s".formatted(error.getField(), error.getDefaultMessage()))
                            .toList();

                    errorAttributes.put("errors", simplifiedErrors);
                }

                return errorAttributes;
            }
        };
    }
}
