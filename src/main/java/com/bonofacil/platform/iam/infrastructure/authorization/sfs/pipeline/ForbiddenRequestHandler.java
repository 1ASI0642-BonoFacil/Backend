package com.bonofacil.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.bonofacil.platform.shared.interfaces.rest.resources.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ForbiddenRequestHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForbiddenRequestHandler.class);
    private final ObjectMapper objectMapper;

    public ForbiddenRequestHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOGGER.error("Access Denied: {}", accessDeniedException.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.FORBIDDEN.value(),
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), errorMessage);
    }
}
