package com.chatapp.messageservice.controller;

import com.chatapp.messageservice.model.MessageEntity;
import com.chatapp.messageservice.repository.MessageRepository;
import com.chatapp.messageservice.service.UserClientService;
import com.chatapp.shared.grpc.UserProfileResponse;
import com.chatapp.messageservice.config.RabbitMQConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private UserClientService userClientService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public String sendMessage(@RequestParam String userId, @RequestParam String content) {

        try {
            MessageEntity message = new MessageEntity();

            // --- BAKDÖRR FÖR BOTEN (VG-KRAV) ---
            if ("bot-001".equals(userId)) {
                message.setSenderId("bot-001");
                message.setSenderUsername("ChatBot"); // Hårdkodat namn för boten
                message.setContent(content);
                message.setTimestamp(LocalDateTime.now());
            }
            else {
                // --- VANLIG LOGIK FÖR ANVÄNDARE (gRPC + Verifiering) ---
                // 1. Anropa user-service via gRPC för att hämta användarens profil
                UserProfileResponse userProfile = userClientService.getUserProfile(userId);

                // 2. Förbered meddelandeobjektet med data från gRPC
                message.setSenderId(userProfile.getUserId());
                message.setSenderUsername(userProfile.getUsername());
                message.setContent(content);
                message.setTimestamp(LocalDateTime.now());
            }

            // 3. Spara i message-services egna databas (MessageDB)
            MessageEntity savedMessage = messageRepository.save(message);

            // 4. Publicera händelsen till Message Queue
            // Vi skickar händelsen som ett JSON-paket
            String eventPayload = String.format(
                    "{\"senderUsername\": \"%s\", \"content\": \"%s\", \"timestamp\": \"%s\"}",
                    savedMessage.getSenderUsername(),
                    savedMessage.getContent(),
                    savedMessage.getTimestamp()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "message.published",
                    eventPayload
            );

            return String.format(
                    "Meddelande sparat och händelse publicerad!\n" +
                            "ID: %d | Avsändare: %s",
                    savedMessage.getId(),
                    savedMessage.getSenderUsername()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return "Kunde inte skicka meddelande: " + e.getMessage();
        }
    }

    @GetMapping("/history")
    public List<MessageEntity> getMessageHistory() {
        return messageRepository.findAllByOrderByTimestampAsc();
    }
}