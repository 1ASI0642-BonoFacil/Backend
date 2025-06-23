package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Clase DTO para representar métricas de duración y convexidad en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DuracionConvexidadResource {
    
    private BigDecimal duracion;
    private BigDecimal convexidad;
    private BigDecimal tasaMercado;
    
    // Información para análisis de sensibilidad
    private BigDecimal cambioPuntosPorcentuales;
    private BigDecimal cambioPorcentualPrecio;
} 