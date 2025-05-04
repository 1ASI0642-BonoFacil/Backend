package com.bonofacil.platform.profiles.domain.model.valueobjects;

public record RazonSocial(String value) {
    public RazonSocial {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("La Razón Social no puede estar vacía.");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("La Razón Social no puede exceder los 255 caracteres.");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}