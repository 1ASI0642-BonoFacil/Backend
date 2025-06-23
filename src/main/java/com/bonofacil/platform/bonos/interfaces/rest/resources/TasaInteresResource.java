package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * Clase DTO para representar una tasa de interés en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TasaInteresResource {
    
    /**
     * Tipo de tasa de interés.
     * EFECTIVA: Tasa efectiva.
     * NOMINAL: Tasa nominal.
     */
    public enum TipoTasa {
        EFECTIVA,
        NOMINAL
    }
    
    @NotNull(message = "El valor de la tasa es obligatorio")
    @Positive(message = "El valor de la tasa debe ser positivo")
    private BigDecimal valor;
    
    @NotNull(message = "El tipo de tasa es obligatorio")
    private TipoTasa tipo;
    
    @NotNull(message = "La frecuencia de capitalización es obligatoria")
    @Positive(message = "La frecuencia de capitalización debe ser positiva")
    private Integer frecuenciaCapitalizacion;
} 