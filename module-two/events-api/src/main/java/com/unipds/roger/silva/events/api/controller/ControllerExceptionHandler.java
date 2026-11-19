package com.unipds.roger.silva.events.api.controller;

import com.unipds.roger.silva.events.api.dto.ErrorDTO;
import com.unipds.roger.silva.events.api.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(exception = NotFoundException.class)
    public ResponseEntity<ErrorDTO> handlerNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorDTO.builder().message(ex.getMessage()).build());
    }
}
