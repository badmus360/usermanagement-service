package com.fintech.usermanagement.util;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();
        contact.setEmail("itaccounts@9psb.com.ng");
        contact.setName("9PSB");
        contact.setUrl("https://www.9psb.com.ng");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("User Management API")
                .version("2.0")
                .contact(contact)
                .description("This API exposes User Management API")
                .termsOfService("https://www.9psb.com.ng/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .specVersion(SpecVersion.V30)
                .addSecurityItem(new SecurityRequirement()
                        .addList(AUTHORIZATION_HEADER))
                .components(new Components()
                        .addSecuritySchemes(AUTHORIZATION_HEADER, new SecurityScheme()
                                .name(AUTHORIZATION_HEADER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")
                        )
                );
    }
}
