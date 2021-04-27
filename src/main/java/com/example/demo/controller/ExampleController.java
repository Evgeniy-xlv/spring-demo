package com.example.demo.controller;

import com.example.demo.annotation.RateLimited;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Example controller demonstrates how the {@link RateLimited} tool works
 * */
@Controller
@RequiredArgsConstructor
public class ExampleController {

    @RateLimited
    @GetMapping("/example")
    public ResponseEntity<Void> exampleMethod() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
