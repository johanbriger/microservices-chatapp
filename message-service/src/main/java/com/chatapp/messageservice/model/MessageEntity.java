package com.chatapp.messageservice.model;

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
    private String senderId; // ID på avsändaren (t.ex. "user-111")

    @Column(nullable = false)
    private String senderUsername; // Vi sparar även namnet för att slippa göra gRPC-anrop varje gång vi läser historik!

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Denna metod körs automatiskt precis innan objektet sparas i databasen
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}