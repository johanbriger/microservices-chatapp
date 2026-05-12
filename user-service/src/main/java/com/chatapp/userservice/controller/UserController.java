package com.chatapp.userservice.controller;

import com.chatapp.userservice.model.UserEntity;
import com.chatapp.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users") // Detta matchar Gatewayens/BFF:ens Path=/users/**
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Hämtar alla användare.
     * Bra för debug och för att se att dina registrerade användare faktiskt hamnar i H2.
     */
    @GetMapping("/all")
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Registrerar en ny användare i User Service egna databas.
     * Denna endpoint anropas av BffController i bff-service.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
        // 1. Validera om användarnamnet redan finns i databasen
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Användarnamnet '" + user.getUsername() + "' är redan upptaget.");
        }

        // 2. Kontrollera om vi behöver generera ett logiskt userId (Business ID)
        // Om frontend/BFF inte skickar med ett (t.ex. "user-111"), skapar vi ett unikt här.
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            // Skapar ett kortare unikt ID, t.ex. user-a1b2c3d4
            String generatedId = "user-" + UUID.randomUUID().toString().substring(0, 8);
            user.setUserId(generatedId);
        }

        // 3. Spara användaren i User DB
        try {
            UserEntity savedUser = userRepository.save(user);
            System.out.println(">> Ny användare sparad i UserDB: " + savedUser.getUsername() + " med ID: " + savedUser.getUserId());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Kunde inte spara användaren: " + e.getMessage());
        }
    }
}