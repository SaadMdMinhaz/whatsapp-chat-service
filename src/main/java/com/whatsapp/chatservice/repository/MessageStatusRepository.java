package com.whatsapp.chatservice.repository;

import com.whatsapp.chatservice.entity.MessageStatus;
import com.whatsapp.chatservice.enums.MessageStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, UUID> {

    List<MessageStatus> findByMessageId(UUID messageId);

    Optional<MessageStatus> findByMessageIdAndRecipientId(UUID messageId, UUID recipientId);

    boolean existsByMessageIdAndRecipientIdAndStatus(
            UUID messageId, UUID recipientId, MessageStatusType status);

    @Query("""
        SELECT COUNT(ms) FROM MessageStatus ms
        JOIN ms.message m
        WHERE m.conversation.id = :conversationId
          AND ms.recipientId = :userId
          AND ms.status <> com.whatsapp.chatservice.enums.MessageStatusType.READ
    """)
    long countUnreadByConversationIdAndUserId(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId
    );

    @Modifying
    @Query("""
        UPDATE MessageStatus ms
        SET ms.status = com.whatsapp.chatservice.enums.MessageStatusType.DELIVERED,
            ms.updatedAt = CURRENT_TIMESTAMP
        WHERE ms.recipientId = :userId
          AND ms.status = com.whatsapp.chatservice.enums.MessageStatusType.SENT
          AND ms.message.conversation.id = :conversationId
    """)
    int markAllAsDelivered(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId
    );

    @Modifying
    @Query("""
        UPDATE MessageStatus ms
        SET ms.status = com.whatsapp.chatservice.enums.MessageStatusType.READ,
            ms.updatedAt = CURRENT_TIMESTAMP
        WHERE ms.recipientId = :userId
          AND ms.status <> com.whatsapp.chatservice.enums.MessageStatusType.READ
          AND ms.message.conversation.id = :conversationId
    """)
    int markAllAsRead(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId
    );

    @Modifying
    @Query("""
        UPDATE MessageStatus ms
        SET ms.status = com.whatsapp.chatservice.enums.MessageStatusType.READ,
            ms.updatedAt = CURRENT_TIMESTAMP
        WHERE ms.message.id = :messageId
          AND ms.recipientId = :userId
          AND ms.status <> com.whatsapp.chatservice.enums.MessageStatusType.READ
    """)
    int markAsRead(
            @Param("messageId") UUID messageId,
            @Param("userId") UUID userId
    );
}
