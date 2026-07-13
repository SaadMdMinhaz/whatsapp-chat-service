package com.whatsapp.chatservice.repository;

import com.whatsapp.chatservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        SELECT m FROM Message m
        WHERE m.conversation.id = :conversationId
        ORDER BY m.createdAt DESC
    """)
    Page<Message> findByConversationIdOrderByCreatedAtDesc(
            @Param("conversationId") UUID conversationId, Pageable pageable);

    @Query("""
        SELECT m FROM Message m
        WHERE m.conversation.id = :conversationId
          AND m.isDeleted = false
        ORDER BY m.createdAt DESC
    """)
    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(
            @Param("conversationId") UUID conversationId);

    @Query("""
        SELECT m FROM Message m
        WHERE m.id = :messageId
    """)
    Optional<Message> findById(@Param("messageId") UUID messageId);
}
