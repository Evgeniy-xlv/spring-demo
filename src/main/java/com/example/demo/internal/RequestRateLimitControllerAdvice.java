package com.example.demo.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * It handles internal exception {@link RequestRateLimitExceededException} and sets response status to 502
 * */
@ControllerAdvice
public class RequestRateLimitControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(RequestRateLimitExceededException.class)
    public void handleRateLimitBlock() {}
}
