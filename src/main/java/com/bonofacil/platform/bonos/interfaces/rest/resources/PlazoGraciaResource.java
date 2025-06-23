package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Clase DTO para representar un plazo de gracia en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlazoGraciaResource {
    
    /**
     * Tipo de plazo de gracia.
     * NINGUNO: Sin plazo de gracia.
     * PARCIAL: Solo se pagan intereses, no capital.
     * TOTAL: No se paga nada, se capitaliza.
     */
    public enum TipoPlazoGracia {
        NINGUNO,
        PARCIAL,
        TOTAL
    }
    
    @NotNull(message = "El tipo de plazo de gracia es obligatorio")
    private TipoPlazoGracia tipo;
    
    @NotNull(message = "El número de períodos es obligatorio")
    @Min(value = 0, message = "El número de períodos no puede ser negativo")
    private Integer periodos;
} 