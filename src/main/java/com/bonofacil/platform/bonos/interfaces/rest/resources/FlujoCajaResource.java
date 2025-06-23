package com.bonofacil.platform.bonos.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase DTO para representar un flujo de caja en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlujoCajaResource {
    
    private Integer periodo;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    private BigDecimal cupon;
    private BigDecimal amortizacion;
    private BigDecimal flujoTotal;
    private BigDecimal saldoInsoluto;
    private BigDecimal valorPresente;
    private BigDecimal factorDescuento;
} 