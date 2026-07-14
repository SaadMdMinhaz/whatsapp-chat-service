package com.whatsapp.chatservice.mapper;

import com.whatsapp.chatservice.dto.response.ParticipantResponse;
import com.whatsapp.chatservice.entity.ConversationParticipant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-14T12:46:14+0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 18.0.2-ea (Private Build)"
)
@Component
public class ConversationMapperImpl implements ConversationMapper {

    @Override
    public ParticipantResponse toParticipantResponse(ConversationParticipant participant) {
        if ( participant == null ) {
            return null;
        }

        UUID userId = null;

        userId = participant.getUserId();

        String username = null;
        String displayName = null;
        String profilePictureUrl = null;

        ParticipantResponse participantResponse = new ParticipantResponse( userId, username, displayName, profilePictureUrl );

        return participantResponse;
    }
}
