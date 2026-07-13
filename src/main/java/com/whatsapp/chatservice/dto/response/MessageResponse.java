package com.whatsapp.chatservice.dto.response;

import com.whatsapp.chatservice.enums.MessageStatusType;
import com.whatsapp.chatservice.enums.MessageType;
import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID senderId,
        String content,
        MessageType messageType,
        String mediaUrl,
        String mediaFileName,
        Long mediaFileSize,
        UUID replyToMessageId,
        MessageResponse repliedMessage,
        boolean isEdited,
        boolean isDeleted,
        MessageStatusType status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
