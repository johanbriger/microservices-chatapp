package com.chatapp.userservice.controller;

import com.chatapp.userservice.model.UserEntity;
import com.chatapp.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users") // Detta matchar Gatewayens/BFF:ens Path=/users/**
public class UserController {


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
        logger.info("Rest-anrop mottaget: Registrera ny användare {}", user.getUsername());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Användarnamnet '" + user.getUsername() + "' är redan upptaget.");
        }

        // Generera ett unikt ID
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            // Skapar ett kortare unikt ID, t.ex. user-a1b2c3d4
            String generatedId = "user-" + UUID.randomUUID().toString().substring(0, 8);
            user.setUserId(generatedId);
        }

        // Spara användaren i User DB
        try {
            UserEntity savedUser = userRepository.save(user);
            System.out.println(">> Ny användare sparad i UserDB: " + savedUser.getUsername() + " med ID: " + savedUser.getUserId());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Kunde inte spara användaren: " + e.getMessage());
        }
    }
}