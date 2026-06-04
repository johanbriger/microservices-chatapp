package com.chatapp.messageservice.controller;

import com.chatapp.messageservice.model.MessageEntity;
import com.chatapp.messageservice.repository.MessageRepository;
import com.chatapp.messageservice.service.UserClientService;
import com.chatapp.shared.grpc.UserProfileResponse;
import com.chatapp.messageservice.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final UserClientService userClientService;
    private final MessageRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

   public MessageController(MessageRepository messageRepository,
                            UserClientService userClientService,
                            RabbitTemplate rabbitTemplate,
                            ObjectMapper objectMapper){
       this.messageRepository = messageRepository;
       this.userClientService = userClientService;
       this.rabbitTemplate = rabbitTemplate;
       this.objectMapper = objectMapper;
   }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String userId, @RequestParam String content) {

       logger.info("Försöker skicka meddelande för userID: {}", userId);
        try {
            MessageEntity message = new MessageEntity();

            if ("bot-001".equals(userId)) {
                message.setSenderId("bot-001");
                message.setSenderUsername("ChatBot"); // Hårdkodat namn för boten
                message.setContent(content);
            }
            else {
                // 1. Anropa user-service via gRPC för att hämta användarens profil
                UserProfileResponse userProfile = userClientService.getUserProfile(userId);

                // 2. Förbered meddelandeobjektet med data från gRPC
                message.setSenderId(userProfile.getUserId());
                message.setSenderUsername(userProfile.getUsername());
                message.setContent(content);
            }

            // 3. Spara i message-services egna databas (MessageDB)
            MessageEntity savedMessage = messageRepository.save(message);

            // 4. Publicera händelsen till Message Queue

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("senderUsername", savedMessage.getSenderUsername());
            messageMap.put("content", savedMessage.getContent());
            messageMap.put("timestamp", savedMessage.getTimestamp().toString());

            String eventPayload = objectMapper.writeValueAsString(messageMap);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "message.published",
                    eventPayload
            );

            logger.info("Meddelande sparat och publicerat. ID: {}" , savedMessage.getId());

            return String.format(
                    "Meddelande sparat och händelse publicerad!\n" +
                            "ID: %d | Avsändare: %s",
                    savedMessage.getId(),
                    savedMessage.getSenderUsername()
            );

        } catch (Exception e) {
            logger.error("Kunde inte skicka meddelande: {}", e.getMessage());
            return "Kunde inte skicka meddelande: " + e.getMessage();
        }
    }

    @GetMapping("/history")
    public List<MessageEntity> getMessageHistory() {
        return messageRepository.findAllByOrderByTimestampAsc();
    }
}