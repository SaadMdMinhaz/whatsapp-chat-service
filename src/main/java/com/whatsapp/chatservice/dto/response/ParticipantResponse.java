package com.whatsapp.chatservice.dto.response;

import java.util.UUID;

public record ParticipantResponse(
        UUID userId,
        String username,
        String displayName,
        String profilePictureUrl
) {
}
