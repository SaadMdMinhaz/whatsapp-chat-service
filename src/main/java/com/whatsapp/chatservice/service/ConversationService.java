package com.whatsapp.chatservice.service;

import com.whatsapp.chatservice.dto.request.CreateConversationRequest;
import com.whatsapp.chatservice.dto.response.ConversationDetailResponse;
import com.whatsapp.chatservice.dto.response.ConversationResponse;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    ConversationResponse createOrGetConversation(UUID userId, CreateConversationRequest request);

    List<ConversationResponse> getConversations(UUID userId);

    ConversationDetailResponse getConversationDetail(UUID userId, UUID conversationId);
}
