package com.bonofacil.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI managementPlatformOpenApi(){

        OpenAPI openApi = new OpenAPI()
                .info(new Info()
                        .title("Bono Facil Platform API")
                        .description("Bono Facil Platform application REST API documentation.")
                        .version("v1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));

        final String securitySchemeName = "bearerAuth";
        Components components = new Components();
        components.addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
        openApi.components(components);

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);
        openApi.addSecurityItem(securityRequirement);

        return openApi;
    }
}
