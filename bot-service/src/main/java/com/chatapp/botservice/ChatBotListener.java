package com.chatapp.botservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class ChatBotListener {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // För att tolka JSON

    @Value("${services.messenger.url}")
    private String messageServiceUrl;

    @RabbitListener(queues = "message-published-queue")
    public void handleMessage(String messageJson) { // Ta emot String istället för Map
        try {
            // Konvertera JSON-strängen till en Map manuellt
            Map<String, Object> messageData = objectMapper.readValue(messageJson, Map.class);

            String content = (String) messageData.get("content");
            String sender = (String) messageData.get("senderUsername");

            if ("ChatBot".equals(sender)) return;

            System.out.println("Boten läste: " + content);

            if (content.toLowerCase().contains("hej") || content.toLowerCase().contains("bot")) {
                sendResponse("Hallå där " + sender + "! Jag är boten som bor i RabbitMQ. Hur kan jag hjälpa dig?");
            }
        } catch (Exception e) {
            System.err.println("Kunde inte tolka meddelandet: " + e.getMessage());
        }
    }

    private void sendResponse(String text) {
        try {
            String url = messageServiceUrl + "/messages/send?userId=bot-001&content=" + text;
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Boten kunde inte svara: " + e.getMessage());
        }
    }
}