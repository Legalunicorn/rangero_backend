package com.hiroc.rangero.aop;


import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.JWTExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AOPExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        return new ErrorDetails("Error due to unique data conflict");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDetails handleBadCredentials(BadCredentialsException ex){
        return new ErrorDetails(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleBadRequest(BadRequestException ex){
        return new ErrorDetails(ex.getMessage());
    }

    @ExceptionHandler(JWTExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleJWTExpired(JWTExpiredException ex){
        return new ErrorDetails(ex.getMessage(),"JWT_EXPIRED");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorDetails handleRTE(RuntimeException ex){
        log.debug("RUNTIME EXCEPTION {}",ex.getMessage());
        return new ErrorDetails(ex.getMessage());

    }
    }
