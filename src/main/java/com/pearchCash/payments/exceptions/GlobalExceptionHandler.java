package com.pearchCash.payments.exceptions;

import com.pearchCash.payments.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        //return ResponseEntity.badRequest().body(errors);
        return new ResponseEntity<>(new Response("400", errors.toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<Response> handleInsufficientBalance(
            UsernameExistsException ex
    ) {

        return new ResponseEntity<>(new Response("10", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Response> handleInsufficientBalance(
            EmailExistsException ex
    ) {

        return new ResponseEntity<>(new Response("09", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Response> handleInsufficientBalance(
            InsufficientBalanceException ex
    ) {

        return new ResponseEntity<>(new Response("05", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Response> handleAccountAlreadyExists(
            AccountAlreadyExistsException ex
    ) {

        return new ResponseEntity<>(new Response("02", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> handleBadCredentials(
            BadCredentialsException ex
    ) {

        return new ResponseEntity<>(new Response("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Response> handleCurrencyMismatch(
            AccountNotFoundException ex
    ) {

        return new ResponseEntity<>(new Response("08", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleValidationErrors(
            ConstraintViolationException ex
    ) {
        /*List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());*/

        return new ResponseEntity<>(new Response("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
