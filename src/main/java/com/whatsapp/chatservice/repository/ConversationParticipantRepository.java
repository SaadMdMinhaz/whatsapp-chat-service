package com.whatsapp.chatservice.repository;

import com.whatsapp.chatservice.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, UUID> {

    List<ConversationParticipant> findByConversationId(UUID conversationId);

    List<ConversationParticipant> findByUserId(UUID userId);

    Optional<ConversationParticipant> findByConversationIdAndUserId(
            UUID conversationId, UUID userId);

    boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);

    @Query("""
        SELECT COUNT(cp) FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
    """)
    long countByConversationId(@Param("conversationId") UUID conversationId);
}
