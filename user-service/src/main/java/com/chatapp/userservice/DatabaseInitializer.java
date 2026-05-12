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
                    null,                  // Long id
                    "user-111",            // String userId
                    "AliceCode",           // String username
                    "alice@example.com",   // String email
                    "password123"          // String password (detta lade vi till i entiteten)
            ));

            userRepository.save(new UserEntity(
                    null,
                    "user-222",
                    "BobDeveloper",
                    "bob@example.com",
                    "password456"
            ));

            System.out.println(">> H2-databasen har initierats med testanvändare och lösenord!");
        }
    }
}