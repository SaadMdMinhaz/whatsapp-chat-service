package com.whatsapp.chatservice.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ConversationListProjection {
    UUID getConversationId();
    String getConversationType();
    UUID getLastMessageId();
    UUID getLastMessageSenderId();
    String getLastMessageContent();
    String getLastMessageType();
    Boolean getLastMessageIsDeleted();
    LocalDateTime getLastMessageCreatedAt();
    Integer getUnreadCount();
    UUID getOtherUserId();
    String getOtherUserDisplayName();
    String getOtherUserProfilePictureUrl();
    String getConversationName();
}
