package com.ipiecoles.java.java350.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(
            EntityNotFoundException entityNotFoundException) {
        return entityNotFoundException.getMessage();
    }

    @ExceptionHandler(EmployeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleEmployeException(
            EmployeException e) {
        return e.getMessage();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(
            IllegalArgumentException e) {
        return e.getMessage();
    }


}

