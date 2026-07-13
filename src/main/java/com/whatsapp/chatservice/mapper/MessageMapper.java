package com.whatsapp.chatservice.mapper;

import com.whatsapp.chatservice.dto.response.MessageResponse;
import com.whatsapp.chatservice.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "repliedMessage", ignore = true)
    MessageResponse toResponse(Message message);
}
