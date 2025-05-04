package com.bonofacil.platform.shared.interfaces.rest.middleware;

import com.bonofacil.platform.shared.application.exceptions.*;
import com.bonofacil.platform.shared.interfaces.rest.resources.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage validationException(ValidationException ex, WebRequest request){
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(InvalidValueException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage invalidValueException(InvalidValueException ex, WebRequest request){
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(ResourceAlreadyException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage resourceAlreadyException(ResourceAlreadyException ex, WebRequest request){
        return new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalExceptionHandler(Exception ex, WebRequest request){
        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedException(UnauthorizedException ex, WebRequest request){
        return new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }
}
