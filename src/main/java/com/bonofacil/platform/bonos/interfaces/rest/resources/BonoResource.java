package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BonoResource {
    private Long id;
    private String nombre;
    private String descripcion;
    private double valorNominal;
    private double tasaCupon;
    private int plazoAnios;
    private int frecuenciaPagos;
    private String moneda;
    private LocalDate fechaEmision;
    private int plazosGraciaTotal;
    private int plazosGraciaParcial;
    private double tcea;
    private double duracion;
    private double convexidad;
    private String emisorUsername;
    private String metodoAmortizacion;
}