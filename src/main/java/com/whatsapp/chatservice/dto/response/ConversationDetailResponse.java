package com.whatsapp.chatservice.dto.response;

import com.whatsapp.chatservice.enums.ConversationType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ConversationDetailResponse(
        UUID id,
        ConversationType type,
        List<ParticipantResponse> participants,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
