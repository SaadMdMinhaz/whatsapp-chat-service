package com.whatsapp.chatservice.mapper;

import com.whatsapp.chatservice.dto.response.MessageResponse;
import com.whatsapp.chatservice.entity.Message;
import com.whatsapp.chatservice.enums.MessageStatusType;
import com.whatsapp.chatservice.enums.MessageType;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-15T14:49:38+0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 18.0.2-ea (Private Build)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageResponse toResponse(Message message) {
        if ( message == null ) {
            return null;
        }

        UUID id = null;
        UUID senderId = null;
        String content = null;
        MessageType messageType = null;
        String mediaUrl = null;
        String mediaFileName = null;
        Long mediaFileSize = null;
        UUID replyToMessageId = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = message.getId();
        senderId = message.getSenderId();
        content = message.getContent();
        messageType = message.getMessageType();
        mediaUrl = message.getMediaUrl();
        mediaFileName = message.getMediaFileName();
        mediaFileSize = message.getMediaFileSize();
        replyToMessageId = message.getReplyToMessageId();
        createdAt = message.getCreatedAt();
        updatedAt = message.getUpdatedAt();

        MessageStatusType status = null;
        MessageResponse repliedMessage = null;
        boolean isEdited = false;
        boolean isDeleted = false;

        MessageResponse messageResponse = new MessageResponse( id, senderId, content, messageType, mediaUrl, mediaFileName, mediaFileSize, replyToMessageId, repliedMessage, isEdited, isDeleted, status, createdAt, updatedAt );

        return messageResponse;
    }
}
