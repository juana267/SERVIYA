package com.upeu.assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AsignacionNoDisponibleException extends RuntimeException {

    public AsignacionNoDisponibleException(String message) {
        super(message);
    }
}
