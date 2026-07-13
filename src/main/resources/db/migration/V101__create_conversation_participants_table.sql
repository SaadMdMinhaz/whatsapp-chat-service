CREATE TABLE conversation_participants (
    id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWSEQUENTIALID(),
    conversation_id UNIQUEIDENTIFIER NOT NULL,
    user_id UNIQUEIDENTIFIER NOT NULL,
    joined_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_conversation_participants PRIMARY KEY CLUSTERED (id),
    CONSTRAINT uq_conversation_participants UNIQUE (conversation_id, user_id),
    CONSTRAINT fk_cp_conversation FOREIGN KEY (conversation_id)
        REFERENCES conversations (id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE NO ACTION
);

CREATE NONCLUSTERED INDEX ix_cp_user_id
    ON conversation_participants (user_id)
    INCLUDE (conversation_id);

CREATE NONCLUSTERED INDEX ix_cp_conversation_id
    ON conversation_participants (conversation_id)
    INCLUDE (user_id);
