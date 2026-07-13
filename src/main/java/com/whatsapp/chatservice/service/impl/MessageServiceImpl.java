package com.whatsapp.chatservice.service.impl;

import com.whatsapp.chatservice.dto.request.EditMessageRequest;
import com.whatsapp.chatservice.dto.request.SendMessageRequest;
import com.whatsapp.chatservice.dto.response.MessagePageResponse;
import com.whatsapp.chatservice.dto.response.MessageResponse;
import com.whatsapp.chatservice.entity.Conversation;
import com.whatsapp.chatservice.entity.Message;
import com.whatsapp.chatservice.entity.MessageStatus;
import com.whatsapp.chatservice.enums.MessageStatusType;
import com.whatsapp.chatservice.enums.MessageType;
import com.whatsapp.chatservice.exception.ConversationNotFoundException;
import com.whatsapp.chatservice.exception.MessageNotFoundException;
import com.whatsapp.chatservice.exception.UnauthorizedConversationException;
import com.whatsapp.chatservice.mapper.MessageMapper;
import com.whatsapp.chatservice.repository.ConversationParticipantRepository;
import com.whatsapp.chatservice.repository.ConversationRepository;
import com.whatsapp.chatservice.repository.MessageRepository;
import com.whatsapp.chatservice.repository.MessageStatusRepository;
import com.whatsapp.chatservice.service.GatewayClient;
import com.whatsapp.chatservice.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;
    private final MessageStatusRepository messageStatusRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageMapper messageMapper;
    private final GatewayClient gatewayClient;

    public MessageServiceImpl(MessageRepository messageRepository,
                              MessageStatusRepository messageStatusRepository,
                              ConversationRepository conversationRepository,
                              ConversationParticipantRepository participantRepository,
                              MessageMapper messageMapper,
                              GatewayClient gatewayClient) {
        this.messageRepository = messageRepository;
        this.messageStatusRepository = messageStatusRepository;
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageMapper = messageMapper;
        this.gatewayClient = gatewayClient;
    }

    @Override
    public MessageResponse sendMessage(UUID userId, UUID conversationId, SendMessageRequest request) {
        log.info("Sending message in conversation: {} by user: {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        "Conversation not found with id: " + conversationId));

        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedConversationException("You are not a participant of this conversation");
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(userId);
        message.setContent(request.content());
        message.setMessageType(request.messageType() != null ? request.messageType() : MessageType.TEXT);
        message.setMediaUrl(request.mediaUrl());
        message.setMediaFileName(request.mediaFileName());
        message.setMediaFileSize(request.mediaFileSize());
        message.setReplyToMessageId(request.replyToMessageId());
        message = messageRepository.save(message);

        List<com.whatsapp.chatservice.entity.ConversationParticipant> participants =
                participantRepository.findByConversationId(conversationId);

        for (com.whatsapp.chatservice.entity.ConversationParticipant participant : participants) {
            if (!participant.getUserId().equals(userId)) {
                MessageStatus status = new MessageStatus();
                status.setMessage(message);
                status.setRecipientId(participant.getUserId());
                status.setStatus(MessageStatusType.SENT);
                messageStatusRepository.save(status);
            }
        }

        log.info("Message sent with id: {}", message.getId());

        for (com.whatsapp.chatservice.entity.ConversationParticipant participant : participants) {
            if (!participant.getUserId().equals(userId)) {
                gatewayClient.deliverMessage(
                        conversationId,
                        message.getId(),
                        userId,
                        message.getContent(),
                        message.getMessageType().name(),
                        message.getMediaUrl(),
                        message.getReplyToMessageId(),
                        participant.getUserId()
                );
            }
        }

        return new MessageResponse(
                message.getId(),
                message.getSenderId(),
                message.getContent(),
                message.getMessageType(),
                message.getMediaUrl(),
                message.getMediaFileName(),
                message.getMediaFileSize(),
                message.getReplyToMessageId(),
                null,
                message.isEdited(),
                message.isDeleted(),
                MessageStatusType.SENT,
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MessagePageResponse getMessages(UUID userId, UUID conversationId, int page, int size) {
        log.debug("Fetching messages for conversation: {} page: {} size: {}", conversationId, page, size);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        "Conversation not found with id: " + conversationId));

        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedConversationException("You are not a participant of this conversation");
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findByConversationIdOrderByCreatedAtDesc(
                conversationId, pageRequest);

        List<MessageResponse> messages = messagePage.getContent().stream()
                .map(messageMapper::toResponse)
                .toList();

        boolean hasNext = messagePage.hasNext();
        String nextCursor = hasNext && !messages.isEmpty()
                ? messages.get(messages.size() - 1).id().toString()
                : null;

        return new MessagePageResponse(messages, hasNext, nextCursor);
    }

    @Override
    public MessageResponse editMessage(UUID userId, UUID messageId, EditMessageRequest request) {
        log.info("Editing message: {} by user: {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "Message not found with id: " + messageId));

        if (!message.getSenderId().equals(userId)) {
            throw new UnauthorizedConversationException("You can only edit your own messages");
        }

        if (message.isDeleted()) {
            throw new IllegalStateException("Cannot edit a deleted message");
        }

        message.setContent(request.content());
        message.setEdited(true);
        message = messageRepository.save(message);

        log.info("Message edited: {}", messageId);
        return messageMapper.toResponse(message);
    }

    @Override
    public void deleteMessage(UUID userId, UUID messageId) {
        log.info("Deleting message: {} by user: {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "Message not found with id: " + messageId));

        if (!message.getSenderId().equals(userId)) {
            throw new UnauthorizedConversationException("You can only delete your own messages");
        }

        message.setDeleted(true);
        message.setContent(null);
        message.setMediaUrl(null);
        message.setMediaFileName(null);
        message.setMediaFileSize(null);
        messageRepository.save(message);

        log.info("Message deleted: {}", messageId);
    }

    @Override
    public void markAsRead(UUID userId, UUID conversationId) {
        log.info("Marking messages as read in conversation: {} for user: {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        "Conversation not found with id: " + conversationId));

        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedConversationException("You are not a participant of this conversation");
        }

        messageStatusRepository.markAllAsDelivered(conversationId, userId);
        int marked = messageStatusRepository.markAllAsRead(conversationId, userId);

        log.info("Marked {} messages as read in conversation: {} for user: {}", marked, conversationId, userId);
    }
}
