package com.whatsapp.chatservice.dto.response;

import com.whatsapp.chatservice.enums.ConversationType;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        ConversationType type,
        ParticipantResponse otherUser,
        MessageResponse lastMessage,
        int unreadCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
