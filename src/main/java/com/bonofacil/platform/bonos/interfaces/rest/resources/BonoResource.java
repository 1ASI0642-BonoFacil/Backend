package com.bonofacil.platform.bonos.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase DTO para representar un bono en la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class BonoResource {
    
    private String id;
    
    @NotBlank(message = "El nombre del bono es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "El valor nominal es obligatorio")
    @Positive(message = "El valor nominal debe ser positivo")
    private BigDecimal valorNominal;
    
    @NotNull(message = "La tasa cupón es obligatoria")
    @PositiveOrZero(message = "La tasa cupón no puede ser negativa")
    private BigDecimal tasaCupon;
    
    @NotNull(message = "El plazo en años es obligatorio")
    @Positive(message = "El plazo en años debe ser positivo")
    private Integer plazoAnios;
    
    @NotNull(message = "La frecuencia de pagos es obligatoria")
    @Positive(message = "La frecuencia de pagos debe ser positiva")
    private Integer frecuenciaPagos;
    
    @NotNull(message = "La fecha de emisión es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;
    
    @PositiveOrZero(message = "La tasa de descuento no puede ser negativa")
    private BigDecimal tasaDescuento;
    
    private String metodoAmortizacion;
    
    // Configuración de plazo de gracia
    private PlazoGraciaResource plazoGracia;
    
    // Configuración de moneda
    @NotNull(message = "La moneda es obligatoria")
    private MonedaResource moneda;
    
    // Configuración de tasa de interés
    @NotNull(message = "La tasa de interés es obligatoria")
    private TasaInteresResource tasaInteres;
}