package com.whatsapp.chatservice.dto.response;

import java.util.List;

public record MessagePageResponse(
        List<MessageResponse> messages,
        boolean hasNext,
        String nextCursor
) {
}
