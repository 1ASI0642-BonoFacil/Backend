package com.bonofacil.platform.profiles.domain.model.valueobjects;

public record Password(String value) {
    public Password {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("La Contraseña no puede estar vacía.");
        }
        if (value.length() < 6) {
            throw new IllegalArgumentException("La Contraseña debe tener al menos 6 caracteres.");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}