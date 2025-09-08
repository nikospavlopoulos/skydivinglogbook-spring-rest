package com.nikospavlopoulos.skydivingrest.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jump")
public class TestOnlyBasicController {

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> getAllJumps() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "controller working");
        return ResponseEntity.ok(response);
    }
}
