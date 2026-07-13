CREATE TABLE messages (
    id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWSEQUENTIALID(),
    conversation_id UNIQUEIDENTIFIER NOT NULL,
    sender_id UNIQUEIDENTIFIER NOT NULL,
    content NVARCHAR(MAX) NULL,
    message_type NVARCHAR(20) NOT NULL DEFAULT 'TEXT',
    media_url NVARCHAR(500) NULL,
    media_file_name NVARCHAR(255) NULL,
    media_file_size BIGINT NULL,
    reply_to_message_id UNIQUEIDENTIFIER NULL,
    is_edited BIT NOT NULL DEFAULT 0,
    is_deleted BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_messages PRIMARY KEY CLUSTERED (id),
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id)
        REFERENCES conversations (id) ON DELETE NO ACTION,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id)
        REFERENCES users (id) ON DELETE NO ACTION,
    CONSTRAINT fk_messages_reply_to FOREIGN KEY (reply_to_message_id)
        REFERENCES messages (id) ON DELETE NO ACTION,
    CONSTRAINT ck_messages_type CHECK (message_type IN ('TEXT', 'IMAGE', 'VIDEO', 'DOCUMENT', 'AUDIO'))
);

CREATE NONCLUSTERED INDEX ix_messages_conversation_created
    ON messages (conversation_id, created_at DESC)
    INCLUDE (sender_id, message_type, is_deleted);

CREATE NONCLUSTERED INDEX ix_messages_reply_to
    ON messages (reply_to_message_id);

CREATE NONCLUSTERED INDEX ix_messages_sender_created
    ON messages (sender_id, created_at DESC)
    INCLUDE (conversation_id);
