package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.dto.response.ErrorResponse;

import br.com.meetime.hubspotintegrator.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HubSpotApiException.class)
    public ResponseEntity<ErrorResponse> handleHubSpotApiException(HubSpotApiException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .messages(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(HubSpotAuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleHubSpotAuthorizationException(HubSpotAuthorizationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .messages(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(HubSpotResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHubSpotResourceNotFoundException(HubSpotResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .messages(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(HubSpotBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleHubSpotBadRequestException(HubSpotBadRequestException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .messages(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .messages(errors)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .messages(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(HubSpotRateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleHubSpotRateLimitExceededException(HubSpotRateLimitExceededException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .messages(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}