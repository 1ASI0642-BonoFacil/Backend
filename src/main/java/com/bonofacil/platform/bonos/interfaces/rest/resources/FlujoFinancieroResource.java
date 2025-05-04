package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FlujoFinancieroResource {
    private Long id;
    private Integer periodo;
    private LocalDate fecha;
    private BigDecimal cuota;
    private BigDecimal amortizacion;
    private BigDecimal interes;
    private BigDecimal saldo;
    private BigDecimal flujo;
    private BigDecimal factorDescuento;
    private BigDecimal valorActual;
    private BigDecimal factorTiempo;
}