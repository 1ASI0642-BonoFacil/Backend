package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Clase DTO para representar información de rendimiento en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendimientoResource {
    
    private BigDecimal tasaRendimiento;
    private BigDecimal precio;
} 