package com.bonofacil.platform.bonos.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object que representa una moneda en el sistema.
 */
public class Moneda {
    private final String codigo;
    private final String nombre;
    private final String simbolo;

    public Moneda(String codigo, String nombre, String simbolo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de moneda no puede ser nulo o vacío");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.simbolo = simbolo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Moneda moneda = (Moneda) o;
        return codigo.equals(moneda.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return simbolo + " (" + codigo + ")";
    }
} 