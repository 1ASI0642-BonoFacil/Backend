package com.bonofacil.platform.bonos.interfaces.rest.resources;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCalculoResource {
    private Long bonoId;
    private double tasaEsperada;
}