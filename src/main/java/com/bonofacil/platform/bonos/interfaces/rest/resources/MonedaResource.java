package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Clase DTO para representar una moneda en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonedaResource {
    
    @NotBlank(message = "El código de moneda es obligatorio")
    private String codigo;
    
    @NotBlank(message = "El nombre de moneda es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El símbolo de moneda es obligatorio")
    private String simbolo;
} 