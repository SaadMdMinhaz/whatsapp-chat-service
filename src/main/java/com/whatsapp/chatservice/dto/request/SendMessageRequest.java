package com.whatsapp.chatservice.dto.request;

import com.whatsapp.chatservice.enums.MessageType;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SendMessageRequest(
        @Size(max = 5000, message = "Message content must not exceed 5000 characters")
        String content,

        MessageType messageType,

        @Size(max = 500, message = "Media URL must not exceed 500 characters")
        String mediaUrl,

        @Size(max = 255, message = "Media file name must not exceed 255 characters")
        String mediaFileName,

        Long mediaFileSize,

        UUID replyToMessageId
) {
}
