package com.bonofacil.platform.profiles.domain.model.valueobjects;

public record Ruc(String value) {
    public Ruc {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUC no puede estar vacío.");
        }
        if (value.length() != 11 || !value.matches("\\d+")) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos numéricos.");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}