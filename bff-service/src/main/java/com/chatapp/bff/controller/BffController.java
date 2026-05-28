package com.chatapp.bff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/bff")
public class BffController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.user.url}")
    private String userServiceUrl;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        // BFF orkestrerar anropet till User Service
        // User Service hanterar lagring i UserDB
        String fullUrl = userServiceUrl + "/users/register";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, registrationData, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("BFF-service kunde inte slutföra registreringen: " + e.getMessage());
        }
    }
}