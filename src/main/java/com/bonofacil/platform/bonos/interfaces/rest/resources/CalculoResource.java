package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CalculoResource {
    private Long id;
    private Long bonoId;
    private String bonoNombre;
    private String inversorUsername;
    private BigDecimal tasaEsperada;
    private BigDecimal trea;
    private BigDecimal precioMaximo;
    private LocalDate fechaCalculo;
    private String informacionAdicional;
}