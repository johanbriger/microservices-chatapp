package com.chatapp.authservice.controller;

import com.chatapp.authservice.dto.LoginRequest;
import com.chatapp.shared.grpc.LoginDetailsRequest;
import com.chatapp.shared.grpc.LoginDetailsResponse;
import com.chatapp.shared.grpc.UserServiceGrpc;
import com.chatapp.authservice.util.JwtUtil;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
// @CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        // 1. Hämta användarinfo via gRPC
        LoginDetailsResponse user = userServiceStub.getLoginDetails(
                LoginDetailsRequest.newBuilder()
                        .setUsername(loginRequest.getUsername())
                        .build()
        );

        // 2. Kontrollera lösenord
        if (user.getPassword().equals(loginRequest.getPassword())) {
            // 3. Generera JWT
            String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());

            // 4. Skapa JSON-svar: { "accessToken": "token-sträng" }
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", token);

            return ResponseEntity.ok(response);
        } else {
            // Returnera 401 Unauthorized om lösenordet är fel
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Fel lösenord eller användarnamn");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}