package org.example.appsubscription.api.controller.handler;

import org.example.appsubscription.api.dto.ErrorMessageDto;
import org.example.appsubscription.api.exception.SubscriptionException;
import org.example.appsubscription.api.exception.SubscriptionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class SubscriptionExceptionHandler {

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handleSubscriptionException(SubscriptionNotFoundException ex) {
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .description(Arrays.toString(ex.getStackTrace()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessageDto);
    }

    @ExceptionHandler(SubscriptionException.class)
    public ResponseEntity<ErrorMessageDto> handleSubscriptionException(SubscriptionException ex) {
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .description(Arrays.toString(ex.getStackTrace()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageDto);
    }
}