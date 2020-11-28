package com.gfg.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> badRequestException(BadRequestException e, WebRequest request) {
        return ResponseEntity.badRequest()
                .body(ErrorMessage.builder()
                        .date(new Date())
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(e.getMessage())
                        .path(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(ProductNotFoundException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorMessage.builder()
                        .date(new Date())
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(e.getMessage())
                        .path(request.getDescription(false))
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> internalServerException(Exception e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorMessage.builder()
                        .date(new Date())
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(e.getMessage())
                        .path(request.getDescription(false))
                        .build()
                );
    }

}
