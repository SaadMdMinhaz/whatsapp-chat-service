package com.whatsapp.chatservice.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateConversationRequest(
        @NotNull(message = "Recipient user ID is required")
        UUID recipientUserId
) {
}
