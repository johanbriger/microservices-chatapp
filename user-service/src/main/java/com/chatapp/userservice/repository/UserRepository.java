package com.chatapp.userservice.repository;

import com.chatapp.userservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    // Vi ärver färdiga metoder som findById, save, deleteById etc.
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUserId(String userId);
}
