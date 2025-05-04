package com.bonofacil.platform.profiles.domain.model.valueobjects;

public record NombreContacto(String value) {
    public NombreContacto {
        if (value != null && value.trim().length() > 255) {
            throw new IllegalArgumentException("El Nombre de Contacto no puede exceder los 255 caracteres.");
        }
    }

    @Override
    public String toString() {
        return value != null ? value.trim() : null;
    }
}