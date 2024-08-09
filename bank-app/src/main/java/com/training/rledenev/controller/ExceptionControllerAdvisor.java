package com.training.rledenev.controller;

import com.training.rledenev.dto.ErrorData;
import com.training.rledenev.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionControllerAdvisor {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorData handleException(Exception exception) {
        return new ErrorData(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
                exception.getMessage(), Arrays.toString(exception.getStackTrace()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorData handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ErrorData(HttpStatus.NOT_FOUND, LocalDateTime.now(),
                exception.getMessage(), Arrays.toString(exception.getStackTrace()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorData handleInsufficientFundsException(InsufficientFundsException exception) {
        return new ErrorData(HttpStatus.NOT_ACCEPTABLE, LocalDateTime.now(),
                exception.getMessage(), Arrays.toString(exception.getStackTrace()));
    }

    @ExceptionHandler({NotOwnerException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorData handleForbiddenException(AccessDeniedException exception) {
        return new ErrorData(HttpStatus.FORBIDDEN, LocalDateTime.now(),
                exception.getMessage(), Arrays.toString(exception.getStackTrace()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorData handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return new ErrorData(HttpStatus.BAD_REQUEST, LocalDateTime.now(),
                exception.getMessage(), Arrays.toString(exception.getStackTrace()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorData handleArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = getMessage(exception);
        return new ErrorData(HttpStatus.BAD_REQUEST, LocalDateTime.now(),
                message, exception.toString());
    }

    private String getMessage(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
