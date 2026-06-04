package com.chatapp.userservice;

import com.chatapp.userservice.model.UserEntity;
import com.chatapp.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Vi skickar 'null' som första argument eftersom databasen
            // genererar det numeriska ID:t automatiskt
            userRepository.save(new UserEntity(
                    null,                  // Long id (UUID genereras automatiskt)
                    "user-111",            // String userId
                    "AliceCode",           // String username
                    "alice@example.com",   // String email
                    "password123",         // String password
                    null                   // LocalDateTime createdAt (sköts av @PrePersist)
            ));

            userRepository.save(new UserEntity(
                    null,
                    "user-222",
                    "BobDeveloper",
                    "bob@example.com",
                    "password456",
                    null
            ));

            System.out.println(">> Postgres-databasen har initierats med testanvändare och lösenord!");
        }
    }
}