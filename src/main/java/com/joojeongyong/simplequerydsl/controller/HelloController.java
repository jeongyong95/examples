package com.joojeongyong.simplequerydsl.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello querydsl!");
    }
}
