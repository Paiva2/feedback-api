package com.app.productfeedback.exceptions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleException(MethodArgumentNotValidException exception) {

        List<String> errors =
                exception.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList();
        Map<String, Object> responseBody = new LinkedHashMap<>();

        responseBody.put("status", 422);
        responseBody.put("errors", errors);

        return new ResponseEntity<>(responseBody, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
