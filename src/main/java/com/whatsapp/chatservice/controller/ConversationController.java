package com.whatsapp.chatservice.controller;

import com.whatsapp.chatservice.constant.ApiConstants;
import com.whatsapp.chatservice.dto.request.CreateConversationRequest;
import com.whatsapp.chatservice.dto.request.CreateGroupRequest;
import com.whatsapp.chatservice.dto.response.ConversationDetailResponse;
import com.whatsapp.chatservice.dto.response.ConversationResponse;
import com.whatsapp.chatservice.service.ConversationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = ApiConstants.API_CONVERSATIONS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class ConversationController {

    private static final Logger log = LoggerFactory.getLogger(ConversationController.class);

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> createOrGetConversation(
            Authentication authentication,
            @Valid @RequestBody CreateConversationRequest request) {
        UUID userId = extractUserId(authentication);
        log.info("POST /chats/conversations - create/get conversation for user: {}", userId);
        ConversationResponse response = conversationService.createOrGetConversation(userId, request);
        return ResponseEntity
                .created(URI.create(ApiConstants.API_CONVERSATIONS_PATH + "/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversations(Authentication authentication) {
        UUID userId = extractUserId(authentication);
        log.info("GET /chats/conversations for user: {}", userId);
        List<ConversationResponse> responses = conversationService.getConversations(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/group")
    public ResponseEntity<ConversationResponse> createGroup(
            Authentication authentication,
            @Valid @RequestBody CreateGroupRequest request) {
        UUID userId = extractUserId(authentication);
        log.info("POST /chats/conversations/group - create group for user: {}", userId);
        ConversationResponse response = conversationService.createGroup(userId, request);
        return ResponseEntity
                .created(URI.create(ApiConstants.API_CONVERSATIONS_PATH + "/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDetailResponse> getConversationDetail(
            Authentication authentication,
            @PathVariable UUID id) {
        UUID userId = extractUserId(authentication);
        log.info("GET /chats/conversations/{} for user: {}", id, userId);
        ConversationDetailResponse response = conversationService.getConversationDetail(userId, id);
        return ResponseEntity.ok(response);
    }

    private UUID extractUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
