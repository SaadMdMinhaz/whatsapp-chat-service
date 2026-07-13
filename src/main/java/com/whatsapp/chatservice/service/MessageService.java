package com.whatsapp.chatservice.service;

import com.whatsapp.chatservice.dto.request.EditMessageRequest;
import com.whatsapp.chatservice.dto.request.SendMessageRequest;
import com.whatsapp.chatservice.dto.response.MessagePageResponse;
import com.whatsapp.chatservice.dto.response.MessageResponse;

import java.util.UUID;

public interface MessageService {

    MessageResponse sendMessage(UUID userId, UUID conversationId, SendMessageRequest request);

    MessagePageResponse getMessages(UUID userId, UUID conversationId, int page, int size);

    MessageResponse editMessage(UUID userId, UUID messageId, EditMessageRequest request);

    void deleteMessage(UUID userId, UUID messageId);

    void markAsRead(UUID userId, UUID conversationId);
}
