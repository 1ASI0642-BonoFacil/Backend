package com.bonofacil.platform.shared.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record SuccessMessage(int statusCode, String message, LocalDateTime timestamp) {
    public SuccessMessage(int statusCode, String message) {
        this(statusCode, message, LocalDateTime.now(ZoneOffset.UTC));
    }
}