package com.whatsapp.chatservice.mapper;

import com.whatsapp.chatservice.dto.response.ParticipantResponse;
import com.whatsapp.chatservice.entity.ConversationParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(target = "userId", source = "userId")
    ParticipantResponse toParticipantResponse(ConversationParticipant participant);
}
