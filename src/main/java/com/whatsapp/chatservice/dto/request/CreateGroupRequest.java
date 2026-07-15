package com.whatsapp.chatservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateGroupRequest(
        @NotBlank(message = "Group name is required")
        @Size(min = 1, max = 100, message = "Group name must be between 1 and 100 characters")
        String name,

        @NotEmpty(message = "At least one participant is required")
        List<UUID> participantIds
) {
}
