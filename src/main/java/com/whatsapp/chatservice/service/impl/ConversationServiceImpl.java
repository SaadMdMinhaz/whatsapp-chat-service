package com.whatsapp.chatservice.service.impl;

import com.whatsapp.chatservice.dto.request.CreateConversationRequest;
import com.whatsapp.chatservice.dto.response.ConversationDetailResponse;
import com.whatsapp.chatservice.dto.response.ConversationResponse;
import com.whatsapp.chatservice.dto.response.MessageResponse;
import com.whatsapp.chatservice.dto.response.ParticipantResponse;
import com.whatsapp.chatservice.entity.Conversation;
import com.whatsapp.chatservice.entity.ConversationParticipant;
import com.whatsapp.chatservice.entity.Message;
import com.whatsapp.chatservice.enums.ConversationType;
import com.whatsapp.chatservice.exception.ConversationNotFoundException;
import com.whatsapp.chatservice.exception.UnauthorizedConversationException;
import com.whatsapp.chatservice.mapper.ConversationMapper;
import com.whatsapp.chatservice.repository.ConversationParticipantRepository;
import com.whatsapp.chatservice.repository.ConversationRepository;
import com.whatsapp.chatservice.repository.MessageRepository;
import com.whatsapp.chatservice.repository.MessageStatusRepository;
import com.whatsapp.chatservice.repository.projection.ConversationListProjection;
import com.whatsapp.chatservice.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationServiceImpl.class);

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final MessageStatusRepository messageStatusRepository;
    private final ConversationMapper conversationMapper;

    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   ConversationParticipantRepository participantRepository,
                                   MessageRepository messageRepository,
                                   MessageStatusRepository messageStatusRepository,
                                   ConversationMapper conversationMapper) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
        this.messageStatusRepository = messageStatusRepository;
        this.conversationMapper = conversationMapper;
    }

    @Override
    public ConversationResponse createOrGetConversation(UUID userId, CreateConversationRequest request) {
        log.info("Creating or getting conversation between {} and {}", userId, request.recipientUserId());

        if (userId.equals(request.recipientUserId())) {
            throw new IllegalArgumentException("Cannot create conversation with yourself");
        }

        java.util.Optional<Conversation> existing = conversationRepository
                .findConversationBetweenUsers(userId, request.recipientUserId(), ConversationType.DIRECT);

        if (existing.isPresent()) {
            log.info("Found existing conversation: {}", existing.get().getId());
            return buildConversationResponse(existing.get(), userId);
        }

        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.DIRECT);
        conversation = conversationRepository.save(conversation);

        ConversationParticipant participant1 = new ConversationParticipant();
        participant1.setConversation(conversation);
        participant1.setUserId(userId);
        participantRepository.save(participant1);

        ConversationParticipant participant2 = new ConversationParticipant();
        participant2.setConversation(conversation);
        participant2.setUserId(request.recipientUserId());
        participantRepository.save(participant2);

        log.info("Created new conversation: {}", conversation.getId());
        return buildConversationResponse(conversation, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations(UUID userId) {
        log.debug("Fetching conversations for user: {}", userId);
        List<ConversationListProjection> projections = conversationRepository.getConversationList(userId);

        List<ConversationResponse> responses = new ArrayList<>();
        for (ConversationListProjection p : projections) {
            ParticipantResponse otherUser = new ParticipantResponse(
                    p.getOtherUserId(),
                    null,
                    p.getOtherUserDisplayName(),
                    p.getOtherUserProfilePictureUrl()
            );

            MessageResponse lastMessage = null;
            if (p.getLastMessageId() != null) {
                lastMessage = new MessageResponse(
                        p.getLastMessageId(),
                        p.getLastMessageSenderId(),
                        p.getLastMessageIsDeleted() ? null : p.getLastMessageContent(),
                        com.whatsapp.chatservice.enums.MessageType.valueOf(p.getLastMessageType()),
                        null, null, null, null, null,
                        false,
                        p.getLastMessageIsDeleted(),
                        null,
                        p.getLastMessageCreatedAt(),
                        null
                );
            }

            ConversationResponse response = new ConversationResponse(
                    p.getConversationId(),
                    com.whatsapp.chatservice.enums.ConversationType.valueOf(p.getConversationType()),
                    otherUser,
                    lastMessage,
                    p.getUnreadCount() != null ? p.getUnreadCount() : 0,
                    null,
                    null
            );
            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversationDetail(UUID userId, UUID conversationId) {
        log.debug("Fetching conversation detail: {} for user: {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        "Conversation not found with id: " + conversationId));

        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedConversationException("You are not a participant of this conversation");
        }

        List<ConversationParticipant> participants = participantRepository.findByConversationId(conversationId);
        List<ParticipantResponse> participantResponses = participants.stream()
                .map(conversationMapper::toParticipantResponse)
                .toList();

        return new ConversationDetailResponse(
                conversation.getId(),
                conversation.getType(),
                participantResponses,
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private ConversationResponse buildConversationResponse(Conversation conversation, UUID userId) {
        List<ConversationParticipant> participants = participantRepository
                .findByConversationId(conversation.getId());

        ParticipantResponse otherUser = null;
        for (ConversationParticipant p : participants) {
            if (!p.getUserId().equals(userId)) {
                otherUser = new ParticipantResponse(p.getUserId(), null, null, null);
                break;
            }
        }

        long unreadCount = messageStatusRepository.countUnreadByConversationIdAndUserId(
                conversation.getId(), userId);

        return new ConversationResponse(
                conversation.getId(),
                conversation.getType(),
                otherUser,
                null,
                (int) unreadCount,
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }
}
