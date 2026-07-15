package com.whatsapp.chatservice.repository;

import com.whatsapp.chatservice.entity.Conversation;
import com.whatsapp.chatservice.enums.ConversationType;
import com.whatsapp.chatservice.repository.projection.ConversationListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
        SELECT c FROM Conversation c
        JOIN ConversationParticipant cp1 ON cp1.conversation = c
        JOIN ConversationParticipant cp2 ON cp2.conversation = c
        WHERE cp1.userId = :user1Id
          AND cp2.userId = :user2Id
          AND c.type = :type
    """)
    Optional<Conversation> findConversationBetweenUsers(
            @Param("user1Id") UUID user1Id,
            @Param("user2Id") UUID user2Id,
            @Param("type") ConversationType type
    );

    @Query(value = """
        WITH UserConversations AS (
            SELECT cp.conversation_id
            FROM conversation_participants cp
            WHERE cp.user_id = :userId
        ),
        LastMessages AS (
            SELECT
                m.conversation_id,
                m.id AS last_message_id,
                m.sender_id AS last_message_sender_id,
                m.content AS last_message_content,
                m.message_type AS last_message_type,
                m.is_deleted AS last_message_is_deleted,
                m.created_at AS last_message_created_at,
                ROW_NUMBER() OVER (
                    PARTITION BY m.conversation_id ORDER BY m.created_at DESC
                ) AS rn
            FROM messages m
            WHERE m.conversation_id IN (SELECT conversation_id FROM UserConversations)
        ),
        UnreadCounts AS (
            SELECT
                m.conversation_id,
                COUNT(*) AS unread_count
            FROM message_status ms
            JOIN messages m ON m.id = ms.message_id
            WHERE ms.recipient_id = :userId
              AND ms.status <> 'READ'
              AND m.conversation_id IN (SELECT conversation_id FROM UserConversations)
            GROUP BY m.conversation_id
        ),
        OtherUsers AS (
            SELECT
                cp1.conversation_id,
                MIN(cp2.user_id) AS other_user_id
            FROM conversation_participants cp1
            JOIN conversation_participants cp2
                ON cp1.conversation_id = cp2.conversation_id
            WHERE cp1.user_id = :userId
              AND cp2.user_id <> :userId
            GROUP BY cp1.conversation_id
        )
        SELECT
            c.id AS conversationId,
            c.type AS conversationType,
            lm.last_message_id AS lastMessageId,
            lm.last_message_sender_id AS lastMessageSenderId,
            lm.last_message_content AS lastMessageContent,
            lm.last_message_type AS lastMessageType,
            lm.last_message_is_deleted AS lastMessageIsDeleted,
            lm.last_message_created_at AS lastMessageCreatedAt,
            COALESCE(uc.unread_count, 0) AS unreadCount,
            ou.other_user_id AS otherUserId,
            CAST(NULL AS NVARCHAR(100)) AS otherUserDisplayName,
            CAST(NULL AS NVARCHAR(500)) AS otherUserProfilePictureUrl,
            c.name AS conversationName
        FROM conversations c
        JOIN UserConversations uc2 ON c.id = uc2.conversation_id
        LEFT JOIN LastMessages lm ON c.id = lm.conversation_id AND lm.rn = 1
        LEFT JOIN UnreadCounts uc ON c.id = uc.conversation_id
        LEFT JOIN OtherUsers ou ON c.id = ou.conversation_id
        ORDER BY COALESCE(lm.last_message_created_at, c.created_at) DESC
        """,
        nativeQuery = true
    )
    List<ConversationListProjection> getConversationList(@Param("userId") UUID userId);
}
