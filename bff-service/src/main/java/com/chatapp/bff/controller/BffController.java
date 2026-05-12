package com.chatapp.bff.controller; // Uppdaterat paketnamn

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/bff")
// @CrossOrigin(origins = "*")
public class BffController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        // BFF orkestrerar anropet till User Service
        // User Service hanterar lagring i UserDB
        String userServiceUrl = "http://localhost:8081/users/register";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(userServiceUrl, registrationData, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("BFF-service kunde inte slutföra registreringen: " + e.getMessage());
        }
    }
}