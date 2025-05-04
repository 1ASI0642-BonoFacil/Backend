package com.bonofacil.platform.shared.application.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
