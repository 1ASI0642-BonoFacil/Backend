package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Clase DTO para representar información de precio de mercado en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrecioMercadoResource {
    
    private BigDecimal precio;
    private BigDecimal tasaMercado;
    private BigDecimal valorNominal;
    
    // Precio como porcentaje del valor nominal
    private BigDecimal precioPorcentaje;
} 