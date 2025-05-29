package com.example.gymmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NotFoundController {
    @RequestMapping("/**")
    public ResponseEntity<String> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Endpoint not found");
    }
}
