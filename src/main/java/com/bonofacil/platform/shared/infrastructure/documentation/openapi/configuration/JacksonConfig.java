package com.bonofacil.platform.shared.infrastructure.documentation.openapi.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuración personalizada de Jackson para la serialización JSON.
 */
@Configuration
public class JacksonConfig {
    
    /**
     * Configura el ObjectMapper para incluir todos los campos en la serialización, incluidos los nulos.
     * 
     * @return Constructor de ObjectMapper configurado
     */
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .failOnUnknownProperties(false)
                .failOnEmptyBeans(false)
                .indentOutput(true); // Para mejor legibilidad en desarrollo
    }
} 