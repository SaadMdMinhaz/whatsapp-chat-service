package com.whatsapp.chatservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class GatewayClient {

    private static final Logger log = LoggerFactory.getLogger(GatewayClient.class);

    private final RestTemplate restTemplate;
    private final String gatewayBaseUrl;

    public GatewayClient(RestTemplate restTemplate,
                         @Value("${gateway-service.base-url}") String gatewayBaseUrl) {
        this.restTemplate = restTemplate;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public void deliverMessage(UUID conversationId, UUID messageId, UUID senderId,
                               String content, String messageType, String mediaUrl,
                               UUID replyToMessageId, UUID recipientId) {
        try {
            Map<String, Object> payload = Map.of(
                    "conversationId", conversationId.toString(),
                    "messageId", messageId.toString(),
                    "senderId", senderId.toString(),
                    "content", content != null ? content : "",
                    "messageType", messageType != null ? messageType : "TEXT",
                    "mediaUrl", mediaUrl != null ? mediaUrl : "",
                    "replyToMessageId", replyToMessageId != null ? replyToMessageId.toString() : "",
                    "recipientId", recipientId.toString()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(gatewayBaseUrl + "/api/v1/gateway/deliver", request, Void.class);

            log.debug("Message delivered to gateway for recipient: {}", recipientId);
        } catch (Exception e) {
            log.warn("Failed to deliver message to gateway: {}", e.getMessage());
        }
    }
}
