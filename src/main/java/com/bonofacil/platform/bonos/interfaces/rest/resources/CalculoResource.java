package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CalculoResource {
    private Long id;
    private Long bonoId;
    private String bonoNombre;
    private String inversorUsername;
    private double tasaEsperada;
    private double trea;
    private double precioMaximo;
    private LocalDate fechaCalculo;
    private String informacionAdicional;
}