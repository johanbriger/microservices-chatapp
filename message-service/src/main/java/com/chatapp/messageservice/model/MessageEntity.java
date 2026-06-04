package com.chatapp.messageservice.model;

import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Låter databasen auto-generera ID (1, 2, 3...)
    private Long id;

    @Column(nullable = false)
    private String senderId; // ID på avsändaren

    @Column(nullable = false)
    private String senderUsername;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Denna metod körs automatiskt precis innan objektet sparas i databasen
    @PrePersist
    protected void onCreate() {
        if(this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}