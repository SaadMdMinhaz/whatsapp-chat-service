package com.whatsapp.chatservice.controller;

import com.whatsapp.chatservice.constant.ApiConstants;
import com.whatsapp.chatservice.dto.request.EditMessageRequest;
import com.whatsapp.chatservice.dto.request.SendMessageRequest;
import com.whatsapp.chatservice.dto.response.MessagePageResponse;
import com.whatsapp.chatservice.dto.response.MessageResponse;
import com.whatsapp.chatservice.service.MessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(ApiConstants.API_CONVERSATIONS_PATH + "/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            Authentication authentication,
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request) {
        UUID userId = extractUserId(authentication);
        log.info("POST /chats/conversations/{}/messages - send message by user: {}", conversationId, userId);
        MessageResponse response = messageService.sendMessage(userId, conversationId, request);
        return ResponseEntity
                .created(URI.create(ApiConstants.API_MESSAGES_PATH + "/" + response.id()))
                .body(response);
    }

    @GetMapping(ApiConstants.API_CONVERSATIONS_PATH + "/{conversationId}/messages")
    public ResponseEntity<MessagePageResponse> getMessages(
            Authentication authentication,
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID userId = extractUserId(authentication);
        log.info("GET /chats/conversations/{}/messages page: {} size: {} for user: {}",
                conversationId, page, size, userId);
        MessagePageResponse response = messageService.getMessages(userId, conversationId, page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping(ApiConstants.API_MESSAGES_PATH + "/{messageId}")
    public ResponseEntity<MessageResponse> editMessage(
            Authentication authentication,
            @PathVariable UUID messageId,
            @Valid @RequestBody EditMessageRequest request) {
        UUID userId = extractUserId(authentication);
        log.info("PUT /chats/messages/{} - edit message by user: {}", messageId, userId);
        MessageResponse response = messageService.editMessage(userId, messageId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(ApiConstants.API_MESSAGES_PATH + "/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            Authentication authentication,
            @PathVariable UUID messageId) {
        UUID userId = extractUserId(authentication);
        log.info("DELETE /chats/messages/{} - delete message by user: {}", messageId, userId);
        messageService.deleteMessage(userId, messageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ApiConstants.API_CONVERSATIONS_PATH + "/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            Authentication authentication,
            @PathVariable UUID conversationId) {
        UUID userId = extractUserId(authentication);
        log.info("PATCH /chats/conversations/{}/read - mark as read by user: {}", conversationId, userId);
        messageService.markAsRead(userId, conversationId);
        return ResponseEntity.ok().build();
    }

    private UUID extractUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
