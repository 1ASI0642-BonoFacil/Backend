package com.bonofacil.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.bonofacil.platform.shared.interfaces.rest.resources.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnauthorizedRequestHandlerEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedRequestHandlerEntryPoint.class);
    private final ObjectMapper objectMapper;

    public UnauthorizedRequestHandlerEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException, ServletException {
        LOGGER.error("Unauthorized request: {}", authenticationException.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                authenticationException.getMessage(),
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), errorMessage);
    }

}
